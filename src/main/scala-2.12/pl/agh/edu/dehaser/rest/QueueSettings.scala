package pl.agh.edu.dehaser.rest

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import pl.agh.edu.dehaser.Reporter
import pl.agh.edu.dehaser.backend.TaskQueue

import scala.concurrent.ExecutionContextExecutor


object QueueSettings {
  lazy val reporter: ActorRef = system.actorOf(Props[Reporter], "reporter")
  val queue: ActorRef = system.actorOf(TaskQueue.props, "queue")

  lazy implicit val system = ActorSystem("Rest", ConfigFactory.load("queue"))
  lazy implicit val materializer = ActorMaterializer()
  lazy implicit val ctx: ExecutionContextExecutor = system.dispatcher
}
