package pl.agh.edu.dehaser

import akka.actor.{ActorRef, FSM, LoggingFSM, Props}

import scala.language.{implicitConversions, postfixOps}

class RangeAggregator(wholeRange: List[BigRange], coordinator: ActorRef, workDetails: WorkDetails)
  extends FSM[AggregatorState, AggregatorData] with LoggingFSM[AggregatorState, AggregatorData] with Dehash {


  startWith(AggregatorStateImpl, AggregatorData(RangeConnector(), wholeRange))

  when(AggregatorStateImpl, stateTimeout = reloadTime) {
    // TODO:  if details == workDetails  might me redundant. Remove in final version
    case Event(RangeChecked(range, details), data@AggregatorData(whole, personalRange, _)) if details == workDetails =>
      val updated = whole.addRange(range)
      if (updated.contains(personalRange)) {
        coordinator ! CheckedPersonalRange
        checkWholeRange(whole)
      }
      //      log.info(s"checked: ${personal.ranges} out of: $personalRange [personal] ")
      stay() using data.copy(wholeRangeConnector = updated)

    case Event(UpdatedRanges(connector, details), data@AggregatorData(whole, _, _)) if details == workDetails =>
      val updated = whole.merge(connector)
      stay() using data.copy(wholeRangeConnector = updated)


    case Event(UpdatePersonalRange(newRange, details), data) if details == workDetails =>
      stay() using data.copy(personalRange = newRange)

    case Event(StateTimeout, AggregatorData(whole, _, Some(parentAggregator))) =>
      parentAggregator ! UpdatedRanges(whole, workDetails)
      stay()

    case Event(StateTimeout, AggregatorData(_, _, None)) =>
      log.debug("I'm master aggregator and I got state timeout")
      stay()

    case Event(update: Update, AggregatorData(whole, _, _)) =>
      sender() ! whole.ranges
      stay()

    case Event(SetParentAggregator(pAggregator, details), data) if details == workDetails =>
      stay() using data.copy(parentAggregator = Some(pAggregator))

    case Event(ImLeaving, AggregatorData(whole, _, Some(parentAggregator))) =>
      parentAggregator ! UpdatedRanges(whole, workDetails)
      stop()


    case msg => log.error(s"\n\n\n\n\nNobody expects Spanish Inquisition: $msg\n\n\n\n\n\n\n")
      stop()
  }

  private def checkWholeRange(wholeRangeConnector: RangeConnector) = {
    if (wholeRangeConnector.contains(wholeRange)) coordinator ! CheckedWholeRange
  }

  initialize()
}

object RangeAggregator {
  def props(wholeRange: List[BigRange], coordinator: ActorRef, workDetails: WorkDetails): Props =
    Props(new RangeAggregator(wholeRange, coordinator, workDetails))
}


sealed trait AggregatorState

case object AggregatorStateImpl extends AggregatorState

case class AggregatorData(wholeRangeConnector: RangeConnector, personalRange: List[BigRange], parentAggregator: Option[ActorRef] = None)
