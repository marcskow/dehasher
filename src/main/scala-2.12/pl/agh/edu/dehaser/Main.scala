package pl.agh.edu.dehaser


import akka.actor.{ActorPath, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

object Main {


  def startRestSystem() ={
    val system = ActorSystem()
    protected val ctx : ExecutionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

  }


  def main(args: Array[String]): Unit = {
    if (args.headOption.contains("Queue"))
      startQueueSystem()
    else
      startCoordinatorSystem()
  }

  def startQueueSystem(): Unit = {
    val system = ActorSystem("QueueSystem",
      ConfigFactory.load("queue"))
    val queue = system.actorOf(TaskQueue.props, "queue")
    val reporter = system.actorOf(Props[Reporter], "reporter")
    queue ! DehashIt("a48dbf15d3c2e171b9328005d5727589903c0083b524efba66ea1516231bca85", "SHA-256", reporter) // dupa1
    queue ! DehashIt("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", "SHA-256", reporter) // abc
    queue ! DehashIt("90b94d224ee82c837143ea6f0308c596f0142612678a036c65041b246d52df22", "SHA-256", reporter) // dupsko
    println("Started queueSystem - waiting for messages")
  }

  def startCoordinatorSystem(): Unit = {
    val a_z = "abcdefghijklmnopqrstuvwxyz"

    val system =
      ActorSystem("coordinatorSystem", ConfigFactory.load("coord"))
    val remotePath = ActorPath.fromString("akka.tcp://QueueSystem@127.0.0.1:2552/user/queue")
    system.actorOf(CoordinatorFSM.props(alphabet = a_z, queuePath = remotePath), "coordinator")

    // TODO: change java serializer to sth else
  }
}
