package pl.agh.edu.dehaser

import akka.actor.ActorRef

import scala.collection.immutable.NumericRange


sealed trait Messages

case class ComputeRange(start: String, end: String, hashToCrack: String, algorithm: String) extends Messages

case class giveHalf(actorRef: ActorRef) extends Messages

case class DehashIt(hash: String, algo: String) extends Messages

case class Check(range: NumericRange[Long], hash: String, algo: String)

// TODO: send original hash and algo or not?
case class FoundIt(crackedPass: String)

case class RangeChecked(range: NumericRange[Long])
