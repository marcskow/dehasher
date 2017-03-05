package pl.agh.edu.dehasher.messages

import akka.actor.ActorRef


// TODO: send original hash and algo or not?
case class FoundIt(crackedPass: String)


case class WorkDetails(hash: String, algo: String, alphabet: String)


case object CancelComputation


case object GiveMeRange

case class DehashIt(hash: String, algo: String, originalSender: ActorRef, maxNrOfChars: Int, alphabet: String)
