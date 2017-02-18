package pl.agh.edu.dehaser

import akka.actor.{ActorPath, ActorRef, FSM, LoggingFSM, PoisonPill, Props}

import scala.concurrent.duration._
import scala.language.postfixOps

class CoordinatorFSM(alphabet: String, nrOfWorkers: Int, queuePath: ActorPath)
  extends FSM[CoordinatorState, CoordinatorData] with Dehash
    with LoggingFSM[CoordinatorState, CoordinatorData] {

  private val queue = context.actorSelection(queuePath)
  private val slaves = (1 to nrOfWorkers).map(_ => context.actorOf(DehashWorker.props(alphabet))).toSet

  startWith(Idle, Uninitialized)

  when(Idle, stateTimeout = 30 seconds) {
    case Event(DehashIt(hash, algo, originalSender), _) =>
      log.info(s"I'm now master coordinator of: hash: $hash algo: $algo")
      val wholeRange = BigRange(1, nrOfIterations(maxNrOfChars))
      val details = WorkDetails(hash, algo)
      val aggregator = context.actorOf(RangeAggregator.props(List(wholeRange), self, details))
      goto(Master) using ProcessData(subContractors = Set.empty[ActorRef],
        details, BigRangeIterator(wholeRange), parent = originalSender,
        masterCoordinator = self, aggregator)

    case Event(AskHim(otherCoordinator), _) =>
      otherCoordinator ! GiveHalf
      stay()

    case Event(Invalid | StateTimeout, _) => goto(Idle)

    case Event(CheckHalf(range, details, master, parentAggregator), _) =>
      log.info(s"Started processing chunk: $range, : details: $details")
      val aggregator = context.actorOf(RangeAggregator.props(range, self, details))
      aggregator ! SetParentAggregator(parentAggregator, details)
      goto(ChunkProcessing) using ProcessData(subContractors = Set.empty[ActorRef], details,
        BigRangeIterator(range), parent = sender(), master, aggregator)

    case Event(GiveHalf, _) => sender() ! Invalid
      stay()
  }

  // TODO: subcontracotrs id map actorRef => personal range
  // TODO: parent is watching child for failure
  // TODO: children are watching parent for failure, and go to master
  when(Master) {
    case Event(FoundIt(crackedPass), ProcessData(subContractors, _, _, client, _, aggregator)) =>
      client ! Cracked(crackedPass)
      subContractors.foreach(_ ! CancelComputation)
      endNode(aggregator)

    case Event(EverythingChecked, ProcessData(_, _, _, client, _, aggregator)) =>
      client ! NotFoundIt
      endNode(aggregator)

    case Event(IamYourNewChild, data@ProcessData(subContractors, details, _, _, _, aggregator)) =>
      sender() ! SetParentAggregator(aggregator, details)
      stay() using data.copy(subContractors = subContractors + sender())
    // TODO: subcontracotrs id map actorRef => personal range


    // todo go to some waiting state and wait for others to complete (when range connector will be full) after everything
    // TODO: master check every 60 second, if some ranges were't lost, and retransmits them into queue if needed
  }


  when(ChunkProcessing) {
    case Event(foundIt: FoundIt, ProcessData(subContractors, _, _, _, master, aggregator)) =>
      master ! foundIt
      leave(subContractors, aggregator)

    case Event(ImLeaving, data@ProcessData(_, _, _, _, master, _)) =>
      master ! IamYourNewChild
      stay() using data.copy(parent = master)

    case Event(msg: SetParentAggregator, data: ProcessData) =>
      data.aggregator ! msg
      stay()

    case Event(EverythingChecked, ProcessData(subContractors, _, _, _, _, aggregator)) =>
      leave(subContractors, aggregator)
  }


  private def leave(subContractors: Set[ActorRef], aggregator: ActorRef) = {
    subContractors.foreach(_ ! ImLeaving)
    aggregator ! ImLeaving
    goto(Idle) using Uninitialized
  }

  onTransition {
    case _ -> Idle => queue ! GiveMeWork
    case Idle -> (Master | ChunkProcessing) =>
      slaves.foreach(_ ! WorkAvailable)
      queue ! OfferTask

    case (Master -> Master | ChunkProcessing -> ChunkProcessing) => nextStateData match {
      case ProcessData(_, _, iterator, _, _, _) =>
        if (iterator.totalLength > splitThreshold) {
          queue ! OfferTask
        }
      case Uninitialized => log.error("\n\n\n\n\nYou didn't expect me here\n\n\n\n\n\n\n")
        stop()
    }
  }


  whenUnhandled {
    case Event(GiveHalf, data@ProcessData(subContractorsCurrent, details, iter, _, master, aggregator)) =>
      iter.split() match {
        case Some((first, second)) =>
          sender() ! CheckHalf(second, details, master, aggregator)
          aggregator ! UpdatePersonalRange(first, details)
          goto(stateName) using data.copy(subContractors = subContractorsCurrent + sender(),
            iterator = BigRangeIterator(first))

        case None => sender() ! Invalid
          stay()
      }

    case Event(CancelComputation, ProcessData(subContractors, _, _, _, _, aggregator)) =>
      subContractors.foreach(_ ! CancelComputation)
      endNode(aggregator)

    case Event(GiveMeRange, data@ProcessData(_, details, iterator, _, _, _)) =>
      val (atom, iter) = iterator.next()
      atom.foreach(x => sender() ! Check(x, details))
      stay() using data.copy(iterator = iter)

    case Event(rangeChecked@RangeChecked(_, details), data: ProcessData) if details == data.workDetails =>
      data.aggregator ! rangeChecked
      val (atom, iter) = data.iterator.next()
      atom.foreach(x => sender() ! Check(x, data.workDetails))
      stay() using data.copy(iterator = iter)

    case Event(RangeChecked(range, details), _) =>
      log.debug(s"I got range: $range about $details, but it is not processed anymore, so I ignore this message")
      stay()

    case msg => log.error(s"unhandled msg:$msg")
      stay()
  }

  initialize()


  private def endNode(aggregator: ActorRef) = {
    aggregator ! PoisonPill
    goto(Idle) using Uninitialized
  }


  private def nrOfIterations(maxStringSize: Int): BigInt = {
    (1 to maxStringSize).map(x => BigInt(math.pow(alphabet.length, x).toLong)).sum
  }
}

object CoordinatorFSM {
  def props(alphabet: String, nrOfWorkers: Int = 4, queuePath: ActorPath): Props =
    Props(new CoordinatorFSM(alphabet, nrOfWorkers, queuePath))
}
