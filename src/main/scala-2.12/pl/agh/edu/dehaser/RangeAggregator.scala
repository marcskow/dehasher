package pl.agh.edu.dehaser

import akka.actor.{ActorLogging, ActorRef, FSM, Props}

import scala.concurrent.duration._
import scala.language.postfixOps

class RangeAggregator(wholeRange: List[BigRange], coordinator: ActorRef, workDetails: WorkDetails) extends
  FSM[AggregatorState, AggregatorData] with ActorLogging {

  startWith(AggregatorStateImpl, AggregatorData(RangeConnector(), RangeConnector(), personalRange = wholeRange))

  when(AggregatorStateImpl, stateTimeout = 30 seconds) {
    case Event(RangeChecked(range, details), AggregatorData(whole, personal, personalRange)) if details == workDetails =>
      val updatedWhole = whole.addRange(range)
      val updatedPersonal = personal.addRange(range)
      if (updatedPersonal.contains(personalRange)) {
        coordinator ! EverythingChecked
      }
      log.info(s"checked: ${personal.ranges} out of: $personalRange [personal] ")
      goto(AggregatorStateImpl) using AggregatorData(updatedWhole, updatedPersonal, personalRange)

    case _ => log.error("\n\n\n\n\nNobody expects Spanish Inquisition\n\n\n\n\n\n\n")
      stop()
  }

  initialize()
}

object RangeAggregator {
  def props(wholeRange: List[BigRange], master: ActorRef, workDetails: WorkDetails): Props =
    Props(new RangeAggregator(wholeRange, master, workDetails))
}


sealed trait AggregatorState

case object AggregatorStateImpl extends AggregatorState

case class AggregatorData(wholeRangeConnector: RangeConnector,
                          personalRangeConnector: RangeConnector, personalRange: List[BigRange])