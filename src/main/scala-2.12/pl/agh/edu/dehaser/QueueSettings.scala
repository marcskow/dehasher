package pl.agh.edu.dehaser

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by razakroner on 2017-02-18.
  */
object QueueSettings {
  val system = ActorSystem("QueueSystem", ConfigFactory.load("queue"))
  val queue = system.actorOf(TaskQueue.props, "queue")
  val reporter = system.actorOf(Props[Reporter], "reporter")
}
