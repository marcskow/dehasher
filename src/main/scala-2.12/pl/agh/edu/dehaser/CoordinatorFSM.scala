package pl.agh.edu.dehaser

import akka.actor.{ActorLogging, ActorPath, ActorRef, FSM, PoisonPill, Props}

class CoordinatorFSM(alphabet: String, nrOfWorkers: Int, queuePath: ActorPath, slaveProps: (String => Props))
  extends FSM[CoordinatorState, CoordinatorData] with Dehash with ActorLogging {

  private val queue = context.actorSelection(queuePath)
  private val slaves = (1 to nrOfWorkers).map(_ => context.actorOf(slaveProps(alphabet))).toSet


  startWith(Idle, Uninitialized)

  // todo maybe timeout on waiting for responses, and after timeout ask again
  when(Idle) {
    case Event(DehashIt(hash, algo, originalSender), _) =>
      val wholeRange = BigRange(1, nrOfIterations(maxNrOfChars))

      val aggregator = context.actorOf(RangeAggregator.props(wholeRange, self))
      goto(Master) using ProcessData(subContractors = Set.empty[ActorRef],
        RangeConnector(), WorkDetails(hash, algo),
        wholeRange, BigRangeIterator(wholeRange),
        parent = originalSender, masterCoordinator = self, aggregator)

    case Event(AskHim(otherCoordinator), _) =>
      otherCoordinator ! GiveHalf
      stay()

    case Event(Invalid, _) => goto(Idle)

    case Event(CheckHalf(range, details, master, aggregator), _) =>
      goto(ChunkProcessing) using ProcessData(subContractors = Set.empty[ActorRef],
        RangeConnector(), details, range, BigRangeIterator(range),
        parent = sender(), master, aggregator)

  }


  when(Master) {
    case Event(FoundIt(crackedPass), ProcessData(subContractors, _, _, _, _, client, _, aggregator)) =>
      client ! Cracked(crackedPass)
      subContractors.foreach(_ ! CancelComputaion)
      aggregator ! PoisonPill
      goto(Idle) using Uninitialized


    case Event(EverythingChecked, ProcessData(subContractors, _, _, _, _, client, _, aggregator)) =>
      client ! NotFoundIt
      aggregator ! PoisonPill
      goto(Idle) using Uninitialized


    case Event(rangeChecked@RangeChecked(range), p@ProcessData(_, rangeConnector, details, _, iterator, _, _, aggregator)) =>
      val updatedRange = rangeConnector.addRange(range)
      val (atom, iter) = iterator.next
      atom.foreach(x => sender() ! Check(x, details))
      aggregator ! rangeChecked
      goto(stateName) using p.copy(iterator = iter, rangeConnector = updatedRange)
    // todo go to some waiting state and wait for others to complete (when range connector will be full) after everything
    // TODO: master check every 60 second, if some ranges were't lost, and retransmits them into queue if needed
  } // todo how to end master when pass not found?


  when(ChunkProcessing) {
    case Event(foundIt: FoundIt, ProcessData(subContractors, _, _, _, _, _, master, _)) =>
      master ! foundIt
      Leave(subContractors)

    case Event(ImLeaving, p@ProcessData(_, _, _, _, _, _, master, _)) =>
      stay() using p.copy(parent = master)


    case Event(checked@RangeChecked(range), p@ProcessData(subContractors, rangeConnector, details, rangeToCheck, iterator, parent, _, aggregator)) =>
      val updatedRange = rangeConnector.addRange(range)
      aggregator ! checked
      if (updatedRange.contains(rangeToCheck)) {

        parent ! DidMyWork(rangeToCheck, details) // todo right now nobody cares about it
        Leave(subContractors) // todo go to some waiting sate and wait for slaves to complete (when range conncetor will be full)
      } else {
        val (atom, iter) = iterator.next
        atom.foreach(x => sender() ! Check(x, details))
        goto(stateName) using p.copy(iterator = iter, rangeConnector = updatedRange)
      }

    //      val (atom, maybeIterator) = iterator.next
    //      sender() ! Check(atom, details)
    //      maybeIterator match {
    //        case Some(x) => goto(ChunkProcessing) using p.copy(iterator = x)
    //        case None =>
    //      }

  }

  def nrOfIterations(maxStringSize: Int): BigInt = {
    (1 to maxStringSize).map(x => BigInt(math.pow(alphabet.length, x).toLong)).sum
  }

  onTransition {
    case _ -> Idle => queue ! GiveMeWork
    case Idle -> (Master | ChunkProcessing) =>
      slaves.foreach(_ ! WorkAvailable)
      queue ! OfferTask

    case _ -> (Master | ChunkProcessing) => nextStateData match {
      case ProcessData(_, _, _, range, _, _, _, _) =>
        if ((range.end - range.start) > splitThreshold) {
          queue ! OfferTask
        }
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

    case Event(CancelComputaion, ProcessData(subContractors, _, _, _, _, _, _, _)) =>
      subContractors.foreach(_ ! CancelComputaion)
      goto(Idle) using Uninitialized


    case Event(GiveMeRange, p@ProcessData(_, _, details, _, iterator, _, _, _)) =>
      //      val (atom, maybeIterator) = iterator.next
      //      sender() ! Check(atom, details)
      //      maybeIterator match {
      //        case Some(x) => goto(stateName) using p.copy(iterator = x)
      //        case None =>
      //          log.warning("Sth possibly wrong. Empty range on Start")
      //          goto(Idle) using Uninitialized // todo wait for checking all ranges
      //      }
      val (atom, iter) = iterator.next
      atom.foreach(x => sender() ! Check(x, details))
      goto(stateName) using p.copy(iterator = iter)

    case msg => log.error(s"unhandled msg:$msg")
      stay()
  }


  initialize()

  private def Leave(subContractors: Set[ActorRef]) = {
    subContractors.foreach(_ ! ImLeaving)
    goto(Idle) using Uninitialized
  }


}

object CoordinatorFSM {
  // TODO: maybe remove slave creator ?
  def props(alphabet: String, nrOfWorkers: Int = 4, queuePath: ActorPath,
            slaveProps: (String => Props) = alphabet => DehashWorker.props(alphabet)): Props =
    Props(new CoordinatorFSM(alphabet, nrOfWorkers, queuePath, slaveProps))
}
