package pl.agh.edu.dehaser

import akka.actor.Actor

class Reporter extends Actor {
  override def receive: Receive = {
    case msg => println(msg)
  }
}
