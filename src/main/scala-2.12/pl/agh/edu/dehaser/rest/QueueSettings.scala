package pl.agh.edu.dehaser.rest

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import pl.agh.edu.dehaser.DummyClient
import pl.agh.edu.dehaser.backend.TaskQueue

import scala.concurrent.ExecutionContextExecutor


object QueueSettings {
  lazy val queue: ActorRef = system.actorOf(TaskQueue.props, "queue")
  lazy val reporter: ActorRef = system.actorOf(Props[DummyClient], "reporter")

  lazy implicit val system = ActorSystem("Rest")
  lazy implicit val materializer = ActorMaterializer()
  lazy implicit val ctx: ExecutionContextExecutor = system.dispatcher
  val HOST = "192.168.0.192"
  val PORT = 9000
}
