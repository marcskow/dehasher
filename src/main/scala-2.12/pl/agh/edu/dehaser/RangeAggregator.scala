package pl.agh.edu.dehaser

import akka.actor.{ActorLogging, ActorRef, FSM, Props}

class RangeAggregator(wholeRange: BigRange, master: ActorRef, workDetails: WorkDetails) extends
  FSM[AggregatorState, RangeConnector] with ActorLogging {

  startWith(AggregatorStateImpl, RangeConnector())

  when(AggregatorStateImpl) {
    case Event(RangeChecked(range, details), checkedRange) if details == workDetails =>
      val updatedRange = checkedRange.addRange(range)
      if (updatedRange.contains(wholeRange)) {
        master ! EverythingChecked
      }
      log.info(s"checked: $updatedRange out of: $wholeRange ")
      goto(AggregatorStateImpl) using updatedRange

    case _ => log.error("\n\n\n\n\nUnexpected Message\n\n\n\n\n\n\n")
      stop()
  }

  initialize()
}

object RangeAggregator {
  def props(wholeRange: BigRange, master: ActorRef, workDetails: WorkDetails): Props =
    Props(new RangeAggregator(wholeRange, master, workDetails))
}


sealed trait AggregatorState

case object AggregatorStateImpl extends AggregatorState
