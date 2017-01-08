package pl.agh.edu.dehaser

import akka.actor.{Actor, ActorRef, Props}

class DehashAggregator(replyTo: ActorRef, hash: String, algo: String, atomSize: Int, maxNrOfchars: Int, alphabet: String) extends Actor {


  override def receive: Receive = {
    case FoundIt(crackedPass) =>
      val whoCrackedIt = sender()
      // TODO:  println
      println(s"I've got solution: $crackedPass ")
      replyTo ! (crackedPass, whoCrackedIt)




    // todo only send after accumulatin meaninfull chunk of progress
    case RangeChecked =>

    // todo idea: implement sth like bittorrent incentive mechanism
  }


}

object DehashAggregator {
  def props(replyTo: ActorRef, hash: String, algo: String, atomSize: Int, maxNrOfchars: Int, alphabet: String) =
    Props(new DehashAggregator(replyTo, hash, algo, atomSize, maxNrOfchars, alphabet))
}
