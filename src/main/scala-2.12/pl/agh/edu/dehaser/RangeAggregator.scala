package pl.agh.edu.dehaser

import akka.actor.{ActorRef, FSM, Props}

class RangeAggregator(wholeRange: BigRange, master: ActorRef) extends FSM[AggregatorState, RangeConnector] {

  startWith(AggregatorStateImpl, RangeConnector())

  when(AggregatorStateImpl) {
    case Event(RangeChecked(range), rangeConnector) =>
      val updatedRange = rangeConnector.addRange(range)
      if (updatedRange.contains(wholeRange)) {
        master ! EverythingChecked
      }
      goto(AggregatorStateImpl) using updatedRange

  }

  initialize()
}

object RangeAggregator {
  def props(wholeRange: BigRange, master: ActorRef): Props = Props(new RangeAggregator(wholeRange, master))
}

case class AggregatorData(connector: RangeConnector)

sealed trait AggregatorState

case object AggregatorStateImpl extends AggregatorState
