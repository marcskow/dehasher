package pl.agh.edu.dehaser


import akka.actor.{ActorPath, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Main {
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
    queue ! DehashIt("90b94d224ee82c837143ea6f0308c596f0142612678a036c65041b246d52df22", "SHA-256", reporter)
    println("Started queueSystem - waiting for messages")
  }

  def startCoordinatorSystem(): Unit = {
    val a_z = "abcdefghijklmnopqrstuvwxyz"

    val system =
      ActorSystem("coordinatorSystem", ConfigFactory.load("coord"))
    val remotePath = ActorPath.fromString("akka.tcp://QueueSystem@127.0.0.1:2552/user/queue")
    system.actorOf(CoordinatorFSM.props(alphabet = a_z, queuePath = remotePath), "coordinator")

    println("Coordinator started")
    // TODO: change java serializer to sth else
  }
}
