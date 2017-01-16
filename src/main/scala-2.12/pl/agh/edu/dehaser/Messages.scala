package pl.agh.edu.dehaser

import akka.actor.ActorRef

import scala.collection.immutable.NumericRange


sealed trait CheckResponse

case class DehashIt(hash: String, algo: String, originalSender: ActorRef)

case class AskHim(otherCoordinator: ActorRef)

case class Check(range: NumericRange[BigInt], workDetails: WorkDetails)

case class CheckHalf(range: BigRange, workDetails: WorkDetails, master: ActorRef)

case class WorkDetails(hash: String, algo: String)

case class DidMyWork(range: BigRange, workDetails: WorkDetails)
case object GiveHalf

case object CancelComputaion
case object Invalid

case class Cracked(dehashed: String)

case object NotFoundIt

case object WorkAvailable

case object GiveMeRange

case object ImLeaving

case object IendedMyWork

case object GiveMeWork

case object OfferTask

// TODO: send original hash and algo or not?
case class FoundIt(crackedPass: String) extends CheckResponse

case class RangeChecked(range: NumericRange[BigInt]) extends CheckResponse
