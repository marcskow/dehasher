package pl.agh.edu.dehaser.messages

import akka.actor.ActorRef
import pl.agh.edu.dehaser.BigRange


// TODO: send original hash and algo or not?
case class FoundIt(crackedPass: String)


case class CheckHalf(ranges: List[BigRange], workDetails: WorkDetails, master: ActorRef, parentAggregator: ActorRef)

case class UpdateSubcontractor(personalRange: List[BigRange], workDetails: WorkDetails)

case class YourPersonalRanges(personalRanges: List[BigRange])

case class ComputedDiffs(diffRanges: List[BigRange], updatedPersonalRange: List[BigRange])

case class WorkDetails(hash: String, algo: String)

case object GiveHalf

case object CancelComputation

case class CancelTask(id: Int)

case object Invalid

case object GiveMeRange

case object ImLeaving

case object ImLeavingMsgToParent

case object CheckedPersonalRange

case object CheckedWholeRange

case class IamYourNewChild(personalRange: List[BigRange], child: ActorRef)