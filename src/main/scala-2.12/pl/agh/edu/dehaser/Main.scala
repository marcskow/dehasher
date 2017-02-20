package pl.agh.edu.dehaser


import akka.actor.{ActorPath, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult
import com.typesafe.config.ConfigFactory

object Main extends RestRoutes {



  def main(args: Array[String]): Unit = {
    //    startQueueSystem
    args.headOption match {
      case Some("Queue") => startQueueSystem()
      case None => startCoordinatorSystem()
    }
  }

  def startQueueSystem(): Unit = {
    import QueueSettings._
    val routeFlow = RouteResult.route2HandlerFlow(controllers)
    val bind = Http().bindAndHandle(routeFlow, QueueSettings.HOST, QueueSettings.PORT)

    import scala.util.{Failure, Success}
    bind.onComplete {
      case Success(success) => println(s"Successfully binded to addres ${success.localAddress}")
      case Failure(ex) => println("Failed to bind to address")
    }
    println("Started queueSystem - waiting for messages")
  }

  def startCoordinatorSystem(): Unit = {
    lazy val remotePath: ActorPath = ActorPath.fromString("akka.tcp://Rest@192.168.0.192:2552/user/queue")
    val a_z = "abcdefghijklmnopqrstuvwxyz"

    val system =
      ActorSystem("coordinatorSystem", ConfigFactory.load("coord"))
    system.actorOf(CoordinatorFSM.props(alphabet = a_z, queuePath = remotePath), "coordinator")

    // TODO: change java serializer to sth else
  }


//  def startClientSystem(): Unit = {
//    lazy val remotePath: ActorPath = ActorPath.fromString("akka.tcp://Rest@127.0.0.1:2552/user/queue")
//    val system = ActorSystem("ClientSystem",
//      ConfigFactory.load("client"))
//    val reporter = system.actorOf(Props[Reporter], "reporter")
//
//    while (true) {
//      val queue = system.actorSelection(remotePath)
//      System.out.println("Please write your hash: \n")
//      val hash = scala.io.StdIn.readLine()
//      System.out.println("Please write algorithm [SHA-256 | MD5 |SHA-1]: \n")
//      val algo = scala.io.StdIn.readLine()
//
//      queue ! DehashIt(hash, algo, reporter)
//      System.out.println("Task dispatched \n")
//    }
//
//  }
}
