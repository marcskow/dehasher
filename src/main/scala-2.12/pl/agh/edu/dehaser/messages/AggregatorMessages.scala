package pl.agh.edu.dehaser.messages

import akka.actor.ActorRef
import pl.agh.edu.dehaser.backend.range.{BigRange, RangeConnector}

import scala.collection.immutable.NumericRange

case class Update(taskId: Int)

case class UpdatedRanges(rangeConnector: RangeConnector, workDetails: WorkDetails)

case class UpdatePersonalRange(personalRange: List[BigRange], workDetails: WorkDetails)

case class SetParentAggregator(parentAggregator: ActorRef, workDetails: WorkDetails)

case object UpdateTick

case object GetMyPersonalRanges

case class AddDiffRanges(personalRangeSubcontractor: List[BigRange])

case class RangeChecked(range: NumericRange[BigInt], workDetails: WorkDetails)