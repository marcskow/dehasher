package pl.agh.edu.dehaser

import akka.actor.Actor


class AccessPoint extends Actor {
  override def receive: Receive = {
    case dehashIt(hash, algo) => // create master with balancing router
    // cancel computation
    // see progress
  }
}

