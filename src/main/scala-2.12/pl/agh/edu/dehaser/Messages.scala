package pl.agh.edu.dehaser

import akka.actor.ActorRef

import scala.collection.immutable.NumericRange


sealed trait CheckResponse

// TODO: send original hash and algo or not?
case class FoundIt(crackedPass: String) extends CheckResponse

case class RangeChecked(range: NumericRange[BigInt], workDetails: WorkDetails) extends CheckResponse

case class Check(range: NumericRange[BigInt], workDetails: WorkDetails)

sealed trait ProcessingTask

case class AskHim(otherCoordinator: ActorRef) extends ProcessingTask

case class DehashIt(hash: String, algo: String, originalSender: ActorRef) extends ProcessingTask

case class CheckHalf(ranges: List[BigRange], workDetails: WorkDetails, master: ActorRef, parentAggregator: ActorRef)

case class UpdatedRanges(rangeConnector: RangeConnector, workDetails: WorkDetails)

case class UpdatePersonalRange(personalRange: List[BigRange], workDetails: WorkDetails)

case class UpdateSubcontractor(personalRange: List[BigRange], workDetails: WorkDetails)

case class SetParentAggregator(parentAggregator: ActorRef, workDetails: WorkDetails)

case class WorkDetails(hash: String, algo: String)

case object GiveHalf

case object CancelComputation

case object Invalid

case class Cracked(dehashed: String)

case object NotFoundIt

case object WorkAvailable

case object GiveMeRange

case object ImLeaving

case object ImLeavingMsgToParent

case object GiveMeWork

case object OfferTask

case object EverythingChecked

case class IamYourNewChild(personalRange: List[BigRange])