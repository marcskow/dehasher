package pl.agh.edu.dehaser

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer

/**
  * Created by razakroner on 2017-02-18.
  */
object RestSettings {
  // TODO: move to config
  val HOST = "127.0.0.1"
  val PORT = 9000
  implicit val httpSystem = ActorSystem("Rest")
  implicit val materializer = ActorMaterializer()
  implicit val ctx = httpSystem.dispatcher
}

object QueueSettings {
//  val system = ActorSystem("QueueSystem", ConfigFactory.load("queue"))
  val queue = RestSettings.httpSystem.actorOf(TaskQueue.props, "queue")
  val reporter = RestSettings.httpSystem.actorOf(Props[Reporter], "reporter")
}
