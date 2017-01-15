package pl.agh.edu.dehaser

import akka.actor.{ActorLogging, ActorRef, FSM}

class CoordinatorFSM(alphabet: String) extends FSM[CoordinatorState, CoordinatorData] with Dehash with ActorLogging {
  private val queue = context.actorSelection("/user/queue")
  private val slaves = (1 to 4).map(_ => context.actorOf(DehashWorker.props(alphabet))).toSet


  startWith(Idle, Uninitialized)

  // todo maybe timeout on waiting for responses
  when(Idle) {
    case Event(DehashIt(hash, algo, originalSender), _) =>
      val wholeRange = BigRange(1, nrOfIterations(maxNrOfChars))

      goto(Master) using ProcessData(subContractors = Set.empty[ActorRef],
        RangeConnector(), WorkDetails(hash, algo),
        wholeRange, BigRangeIterable(wholeRange).iterator,
        parent = originalSender, masterCoordinator = self)

    case Event(AskHim(otherCoordinator), _) =>
      otherCoordinator ! GiveHalf
      stay()

    case Event(Invalid, _) => goto(Idle)

    case Event(CheckHalf(range, details, master), _) =>
      goto(ChunkProcessing) using ProcessData(subContractors = Set.empty[ActorRef],
        RangeConnector(), details, range, BigRangeIterable(range).iterator,
        parent = sender(), master)
  }


  // TODO: master check every 60 second, if some ranges were't lost, and retransmits them into queue if needed
  when(Master) {
    case Event(FoundIt(crackedPass), ProcessData(subContractors, _, _, _, _, client, _)) =>
      client ! Cracked(crackedPass)
      subContractors.foreach(_ ! CancelComputaion)
      goto(Idle) using Uninitialized

    // todo how to end master when pass not found?
    case Event(IendedMyRange, _) =>
      log.info("I'm master waiting for others to end")
      stay()
  }

  when(ChunkProcessing) {
    case Event(foundIt: FoundIt, ProcessData(subContractors, _, _, _, _, _, master)) =>
      master ! foundIt
      stay() //todo close all computations here or not?

    case Event(IendedMyRange, ProcessData(subContractors, _, _, _, _, _, _)) =>
      log.info("I'm partial worker whoe ended his job. I'm free")
      subContractors.foreach(_ ! ImLeaving)
      goto(Idle) using Uninitialized


    case Event(ImLeaving, p@ProcessData(_, _, _, _, _, _, master)) =>
      stay() using p.copy(parent = master)

  }

  onTransition {
    case _ -> Idle => queue ! GiveMeWork
    case Idle -> (Master | ChunkProcessing) => slaves.foreach(_ ! WorkAvailable)
    case _ -> (Master | ChunkProcessing) => nextStateData match {
      case ProcessData(_, _, _, range, _, _, _) =>
        if ((range.end - range.start) > splitThreshold) queue ! OfferTask
    }
  }


  whenUnhandled {
    case Event(GiveHalf, data@ProcessData(subContractorsCurrent, _, details, _, iterator, _, master)) =>
      val optionalRanges = iterator.split()
      if (optionalRanges.isDefined) {
        val (first, second) = optionalRanges.get
        sender() ! CheckHalf(second, details, master)
        goto(stateName) using data.copy(subContractors = subContractorsCurrent + sender(),
          iterator = BigRangeIterable(first).iterator, rangeToCheck = first)
      }
      else {
        sender() ! Invalid
        stay()
      }

    case Event(CancelComputaion, ProcessData(subContractors, _, _, _, _, _, _)) =>
      subContractors.foreach(_ ! CancelComputaion)
      goto(Idle) using Uninitialized

    case Event(RangeChecked(range), ProcessData(subContractors, rangeConnector, details, rangeToCheck, iterator, parent, _)) =>
      rangeConnector.addRange(range) // TODO: make immutable
      if (iterator.hasNext) sender() ! Check(iterator.next(), details)
      else {
        parent ! DidMyWork(rangeToCheck, details)
        self ! IendedMyRange
      }
      stay()

    case msg => log.error(s"unhandled msg:$msg")
      stay()
  }


  initialize()

  def nrOfIterations(maxStringSize: Int): BigInt = {
    (1 to maxStringSize).map(x => BigInt(math.pow(alphabet.length, x).toLong)).sum
  }


}
