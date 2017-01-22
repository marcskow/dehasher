package pl.agh.edu.dehaser

import akka.actor.{ActorRef, FSM, Props}

class RangeAggregator(wholeRange: BigRange, master: ActorRef) extends FSM[AggregatorState, RangeConnector] {

  startWith(AggregatorStateImpl, RangeConnector())

  when(AggregatorStateImpl) {
    case Event(RangeChecked(range), checkedRange) =>
      val updatedRange = checkedRange.addRange(range)
      if (updatedRange.contains(wholeRange)) {
        master ! EverythingChecked
      }
      log.info(s"checked: ${checkedRange.ranges} out of: $wholeRange ")
      goto(AggregatorStateImpl) using updatedRange

  }

  initialize()
}

object RangeAggregator {
  def props(wholeRange: BigRange, master: ActorRef): Props = Props(new RangeAggregator(wholeRange, master))
}


sealed trait AggregatorState

case object AggregatorStateImpl extends AggregatorState
