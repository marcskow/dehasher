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

case class Update(taskId: Int)

case class CheckHalf(ranges: List[BigRange], workDetails: WorkDetails, master: ActorRef, parentAggregator: ActorRef)

case class UpdatedRanges(rangeConnector: RangeConnector, workDetails: WorkDetails)

case class UpdatePersonalRange(personalRange: List[BigRange], workDetails: WorkDetails)

case class UpdateSubcontractor(personalRange: List[BigRange], workDetails: WorkDetails)

case class SetParentAggregator(parentAggregator: ActorRef, workDetails: WorkDetails)

case class AddDiffRanges(personalRangeSubcontractor: List[BigRange])

case class ComputedDiffs(diffRanges: List[BigRange])

case class WorkDetails(hash: String, algo: String)

case object GiveHalf

case object CancelComputation

case class CancelTask(id: Int)

case object Invalid

sealed trait Result

case class Cracked(dehashed: String) extends Result

case class Ranges(ranges: List[BigRange]) extends Result

case object NotFoundIt extends Result

case object NonTaken extends Result

case object NonExisting extends Result

case object WorkAvailable

case object GiveMeRange

case object ImLeaving

case object ImLeavingMsgToParent

case object GiveMeWork

case object OfferTask

case object ListTasks

case object CheckedPersonalRange

case object CheckedWholeRange

case class IamYourNewChild(personalRange: List[BigRange])