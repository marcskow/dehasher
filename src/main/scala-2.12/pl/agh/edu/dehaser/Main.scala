package pl.agh.edu.dehaser


import akka.actor.Status.Success
import akka.actor.{ActorPath, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.util.Failure

object Main extends RestRoutes{


  val HOST = "127.0.0.1"
  val PORT = 80

  val remotePath: ActorPath = ActorPath.fromString("akka.tcp://QueueSystem@127.0.0.1:2552/user/queue")

  def main(args: Array[String]): Unit = {
    args.headOption match {
      case Some("Queue") => startQueueSystem()
      case Some("Client") => startClientSystem()
      case None => startCoordinatorSystem()
    }

    val httpSystem = ActorSystem("Rest")
    val materializer = ActorMaterializer()
    val ctx = httpSystem.dispatcher

    val bind = Http().bindAndHandle(controllers, HOST, PORT)

    import scala.util.Success

    bind.onComplete {
      case Success(success) => println(s"Successfully binded to addres ${success.localAddress}")
      case Failure(ex) => println("Failed to bind to address")
    }
  }

  def startQueueSystem(): Unit = {
    val system = ActorSystem("QueueSystem",
      ConfigFactory.load("queue"))
    val queue = system.actorOf(TaskQueue.props, "queue")
    val reporter = system.actorOf(Props[Reporter], "reporter")
    //    queue ! DehashIt("4bc75035d73f6083683e040fc31f28e0ec6d1cbce5cb0a5e2611eb89bceb6c16", "SHA-256", reporter) // testhash
    //    queue ! DehashIt("c3904668eebedc5a443f65243d196157d31d19ad4b0b86eb3957449a652aa284", "SHA-256", reporter) // hardcoded
    //    queue ! DehashIt("cf80cd8aed482d5d1527d7dc72fceff84e6326592848447d2dc0b0e87dfc9a90", "SHA-256", reporter) // testing
    println("Started queueSystem - waiting for messages")
  }

  def startCoordinatorSystem(): Unit = {
    val a_z = "abcdefghijklmnopqrstuvwxyz"

    val system =
      ActorSystem("coordinatorSystem", ConfigFactory.load("coord"))
    system.actorOf(CoordinatorFSM.props(alphabet = a_z, queuePath = remotePath), "coordinator")

    // TODO: change java serializer to sth else
  }


  def startClientSystem(): Unit = {

    val system = ActorSystem("ClientSystem",
      ConfigFactory.load("client"))
    val reporter = system.actorOf(Props[Reporter], "reporter")

    while(true) {
      val queue = system.actorSelection(remotePath)
      System.out.println("Please write your hash: \n")
      val hash = scala.io.StdIn.readLine()
      System.out.println("Please write algorithm [SHA-256 | MD5 |SHA-1]: \n")
      val algo = scala.io.StdIn.readLine()

      queue ! DehashIt(hash, algo, reporter)
      System.out.println("Task dispatched \n")
    }

  }
}
