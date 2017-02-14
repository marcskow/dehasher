package pl.agh.edu.dehaser

import akka.actor.{ActorLogging, ActorPath, ActorRef, FSM, PoisonPill, Props}

import scala.concurrent.duration._
import scala.language.postfixOps

class CoordinatorFSM(alphabet: String, nrOfWorkers: Int, queuePath: ActorPath)
  extends FSM[CoordinatorState, CoordinatorData] with Dehash with ActorLogging {

  private val queue = context.actorSelection(queuePath)
  private val slaves = (1 to nrOfWorkers).map(_ => context.actorOf(DehashWorker.props(alphabet))).toSet


  startWith(Idle, Uninitialized)

  when(Idle, stateTimeout = 30 seconds) {
    case Event(DehashIt(hash, algo, originalSender), _) =>
      log.info(s"I'm now master coordinator of: hash: $hash algo: $algo")
      val wholeRange = BigRange(1, nrOfIterations(maxNrOfChars))
      val details = WorkDetails(hash, algo)
      val aggregator = context.actorOf(RangeAggregator.props(wholeRange, self, details), "aggregator")
      goto(Master) using ProcessData(subContractors = Set.empty[ActorRef],
        RangeConnector(), details,
        wholeRange, BigRangeIterator(wholeRange),
        parent = originalSender, masterCoordinator = self, aggregator)

    case Event(AskHim(otherCoordinator), _) =>
      otherCoordinator ! GiveHalf
      stay()

    case Event(Invalid | StateTimeout, _) => goto(Idle)

    case Event(CheckHalf(range, details, master, aggregator), _) =>
      log.info(s"Started processing chunk: $range, : details: $details")
      goto(ChunkProcessing) using ProcessData(subContractors = Set.empty[ActorRef],
        RangeConnector(), details, range, BigRangeIterator(range),
        parent = sender(), master, aggregator)

    case Event(GiveHalf, _) => sender() ! Invalid
      stay()
  }

  // TODO: range aggregator contains personal and whole range
  // TODO: subcontracotrs id map actorRef => personal range
  // TODO: parent is watching child for failure
  // TODO: childer watching parent for failure, and go to master
  when(Master) {
    case Event(FoundIt(crackedPass), ProcessData(subContractors, _, _, _, _, client, _, aggregator)) =>
      client ! Cracked(crackedPass)
      subContractors.foreach(_ ! CancelComputation)
      endMaster(aggregator)

    case Event(EverythingChecked, ProcessData(_, _, _, _, _, client, _, aggregator)) =>
      client ! NotFoundIt
      endMaster(aggregator)

    case Event(IamYourNewChild, data@ProcessData(subContractors, _, _, _, _, _, _, _)) =>
      stay() using data.copy(subContractors = subContractors + sender())

    case Event(rangeChecked@RangeChecked(range, details), data: ProcessData) if details == data.workDetails =>
      val updatedRange = data.rangeConnector.addRange(range)
      data.aggregator ! rangeChecked
      checkedRange(rangeChecked, data, updatedRange)
    // todo go to some waiting state and wait for others to complete (when range connector will be full) after everything
    // TODO: master check every 60 second, if some ranges were't lost, and retransmits them into queue if needed
  }


  when(ChunkProcessing) {
    case Event(foundIt: FoundIt, ProcessData(subContractors, _, _, _, _, _, master, _)) =>
      master ! foundIt
      Leave(subContractors)

    case Event(ImLeaving, data@ProcessData(_, _, _, _, _, _, master, _)) =>
      master ! IamYourNewChild
      stay() using data.copy(parent = master)

    case Event(checked@RangeChecked(range, details), data@ProcessData(subContractors, rangeConnector, _,
    rangeToCheck, _, _, _, aggregator)) if details == data.workDetails =>
      val updatedRange = rangeConnector.addRange(range)
      aggregator ! checked
      if (updatedRange.contains(rangeToCheck)) Leave(subContractors)
      else checkedRange(checked, data, updatedRange)
  }


  onTransition {
    case _ -> Idle => queue ! GiveMeWork
    case Idle -> (Master | ChunkProcessing) =>
      slaves.foreach(_ ! WorkAvailable)
      queue ! OfferTask

    case (Master -> Master | ChunkProcessing -> ChunkProcessing) => nextStateData match {
      case ProcessData(_, _, _, range, _, _, _, _) =>
        if ((range.end - range.start) > splitThreshold) {
          //todo update for lists
          queue ! OfferTask
        }
      case Uninitialized => log.error("\n\n\n\n\nYou didn't expect me here\n\n\n\n\n\n\n")
        stop()
    }
  }


  whenUnhandled {
    case Event(GiveHalf, data@ProcessData(subContractorsCurrent, _, details, _, iterator, _, master, aggregator)) =>
      val optionalRanges = iterator.split()
      if (optionalRanges.isDefined) {
        val (first, second) = optionalRanges.get
        sender() ! CheckHalf(second, details, master, aggregator)
        goto(stateName) using data.copy(subContractors = subContractorsCurrent + sender(),
          iterator = BigRangeIterator(first), rangeToCheck = first)
      }
      else {
        sender() ! Invalid
        stay()
      }

    case Event(CancelComputation, ProcessData(subContractors, _, _, _, _, _, _, _)) =>
      subContractors.foreach(_ ! CancelComputation)
      goto(Idle) using Uninitialized


    case Event(GiveMeRange, data@ProcessData(_, _, details, _, iterator, _, _, _)) =>
      val (atom, iter) = iterator.next
      atom.foreach(x => sender() ! Check(x, details))
      stay() using data.copy(iterator = iter)

    case Event(RangeChecked(range, details), _) =>
      log.debug(s"I got range: $range about $details, but it is not processed anymore, so I ignore this message")
      stay()

    case msg => log.error(s"unhandled msg:$msg")
      stay()
  }

  initialize()

  private def Leave(subContractors: Set[ActorRef]) = {
    subContractors.foreach(_ ! ImLeaving)
    goto(Idle) using Uninitialized
  }

  private def checkedRange(rangeChecked: RangeChecked, data: ProcessData, updatedRange: RangeConnector) = {
    val (atom, iter) = data.iterator.next
    atom.foreach(x => sender() ! Check(x, data.workDetails))
    stay() using data.copy(iterator = iter, rangeConnector = updatedRange)
  }

  private def endMaster(aggregator: ActorRef) = {
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
