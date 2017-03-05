package pl.agh.edu.dehasher.backend

import akka.actor.{ActorRef, LoggingFSM, Props}
import akka.routing.{Broadcast, FromConfig}
import pl.agh.edu.dehasher.backend.range.{BigRange, BigRangeIterator, RangeConnector}
import pl.agh.edu.dehasher.messages._

class DehashService extends LoggingFSM[ServiceState, ServiceData] with Dehash {
  val workerRouter: ActorRef = context.actorOf(FromConfig.props(Props[DehashWorker]), name = "workerRouter")

  when(Idle) {
    case Event(msg: DehashIt, Uninitialized) =>
      val wholeRange = BigRange(1, nrOfIterations(msg.maxNrOfChars, msg.alphabet))
      val details = WorkDetails(msg.hash, msg.algo, msg.alphabet)
      val replyTo = sender()
      val iterator = BigRangeIterator(Some(wholeRange))
      workerRouter ! Broadcast(WorkAvailable)
      goto(Processing) using ProcessData(iterator, details, replyTo, RangeConnector(List()), wholeRange)
  }

  when(Processing) {
    case Event(GiveMeRange, data: ProcessData) =>
      sendRange(data, sender())

    case Event(RangeChecked(range, details), data: ProcessData) if details == data.workDetails =>
      data.connector.addRange(range)

      if (data.connector.ranges.contains(data.wholeRange)) {
        data.clientRef ! NotFoundIt
        goto(Idle) using Uninitialized
      } else
        sendRange(data, sender())
    // TODO: add fount it
  }

  def nrOfIterations(maxStringSize: Int, alphabet: String): BigInt = {
    (1 to maxStringSize).map(x => BigInt(math.pow(alphabet.length, x).toLong)).sum
  }

  startWith(Idle, Uninitialized)

  private def sendRange(data: ProcessData, recipient: ActorRef) = {
    val (atom, iter) = data.iterator.next()
    atom.foreach { x => recipient ! Check(x, data.workDetails) }
    stay() using data.copy(iterator = iter)
  }

}


sealed trait ServiceData

case class ProcessData(iterator: BigRangeIterator, workDetails: WorkDetails, clientRef: ActorRef,
                       connector: RangeConnector, wholeRange: BigRange) extends ServiceData

case object Uninitialized extends ServiceData

sealed trait ServiceState

case object Idle extends ServiceState

case object Processing extends ServiceState


