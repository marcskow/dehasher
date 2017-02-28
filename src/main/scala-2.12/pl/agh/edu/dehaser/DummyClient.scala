package pl.agh.edu.dehaser

import akka.actor.Actor

class DummyClient extends Actor {
  override def receive: Receive = {
    case msg => println(msg)
  }
}
