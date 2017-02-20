package pl.agh.edu.dehaser

import akka.actor.{ActorPath, ActorRef, FSM, LoggingFSM, PoisonPill, Props, Terminated}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

class CoordinatorFSM(alphabet: String, nrOfWorkers: Int, queuePath: ActorPath)
  extends FSM[CoordinatorState, CoordinatorData] with Dehash
    with LoggingFSM[CoordinatorState, CoordinatorData] {

  val idleReloadTime: FiniteDuration = 3 seconds
  private val queue = context.actorSelection(queuePath)
  private val slaves = (1 to nrOfWorkers).map(_ => context.actorOf(DehashWorker.props(alphabet))).toSet

  startWith(Idle, Uninitialized)


  when(Idle, stateTimeout = idleReloadTime) {
    case Event(DehashIt(hash, algo, originalSender), _) =>
      log.info(s"\n\nI'm now master coordinator of: hash: $hash algo: $algo\n\n")
      val wholeRange = BigRange(1, nrOfIterations(maxNrOfChars))
      val details = WorkDetails(hash, algo)
      val aggregator = context.actorOf(RangeAggregator.props(List(wholeRange), self, details))
      // TODO: watxh  originalSender or not?
      goto(Master) using ProcessData(subContractors = Map.empty[ActorRef, List[BigRange]],
        details, BigRangeIterator(List(wholeRange)), parent = originalSender,
        masterCoordinator = self, aggregator)

    case Event(AskHim(otherCoordinator), _) =>
      otherCoordinator ! GiveHalf
      stay()

    case Event(Invalid | StateTimeout, _) => goto(Idle)
    // TODO: watch your parent!!!!!!!!!!!!!!!!!! 
    case Event(CheckHalf(range, details, master, parentAggregator), _) =>
      log.info(s"\n\nStarted processing chunk: $range, : details: $details\n\n")
      val aggregator = context.actorOf(RangeAggregator.props(range, self, details))
      aggregator ! SetParentAggregator(parentAggregator, details)
      context.watch(sender())
      goto(ChunkProcessing) using ProcessData(subContractors = Map.empty[ActorRef, List[BigRange]], details,
        BigRangeIterator(range), parent = sender(), master, aggregator)

    case Event(GiveHalf, _) => sender() ! Invalid
      stay()
  }

  when(Master) {
    case Event(FoundIt(crackedPass), ProcessData(subContractors, _, _, client, _, aggregator)) =>
      client ! Cracked(crackedPass) //todo it this needed ?
      subContractors.keys.foreach(_ ! CancelComputation)
      aggregator ! PoisonPill
      log.info(s"Found solution $crackedPass")
      goto(WaitingToDie) using Finished(Cracked(crackedPass))

    case Event(CheckedWholeRange, ProcessData(_, _, _, client, _, aggregator)) =>
      client ! NotFoundIt //todo it this needed ?
      aggregator ! PoisonPill
      goto(WaitingToDie) using Finished(NotFoundIt)

    case Event(IamYourNewChild(personalRange, child), data@ProcessData(subContractors, details, _, _, _, aggregator)) =>
      log.info(s"\n\n\n\n\n I have new direct child witch personal range: $personalRange\n\n\n\n\n")
      child ! SetParentAggregator(aggregator, details)
      context.watch(child)
      stay() using data.copy(subContractors = subContractors + (child -> personalRange))

    case Event(update: Update, ProcessData(_, _, _, _, _, aggregator)) =>
      aggregator forward update
      stay()

    case Event(CancelComputation, ProcessData(subContractors, _, _, _, _, aggregator)) =>
      subContractors.keys.foreach(_ ! CancelComputation)
      sender() ! NotFoundIt
      endNode(aggregator)


    // todo go to some waiting state and wait for others to complete (when range connector will be full) after everything
    // TODO: master check every 60 second, if some ranges were't lost, and retransmits them into queue if needed
  }

  when(WaitingToDie) {
    case Event(_: Update, Finished(result)) =>
      sender() ! result
      goto(Idle) using Uninitialized
  }

  when(ChunkProcessing) {
    case Event(foundIt: FoundIt, ProcessData(subContractors, _, _, parent, master, aggregator)) =>
      master ! foundIt
      leave(subContractors, aggregator, parent)

    case Event(ImLeaving, data@ProcessData(_, _, _, parent, master, _)) =>
      log.info("\n\n\n\n My parent Left me !!!!! \n\n\n\n\n\n")
      parentIsGone(data, master)

    case Event(Terminated(actor), data@ProcessData(_, _, _, parent, master, _)) if actor == parent =>
      log.info("\n\n\n\n My parent Died !!!!! \n\n\n\n\n\n")
      parentIsGone(data, master)

    case Event(msg: SetParentAggregator, data: ProcessData) =>
      data.aggregator ! msg
      stay()

    case Event(CheckedPersonalRange, ProcessData(subContractors, _, _, parent, _, aggregator)) =>
      leave(subContractors, aggregator, parent)


  }

  def nrOfIterations(maxStringSize: Int): BigInt = {
    (1 to maxStringSize).map(x => BigInt(math.pow(alphabet.length, x).toLong)).sum
  }

  private def parentIsGone(data: ProcessData, master: ActorRef) = {
    context.unwatch(data.parent)
    import akka.pattern.{ask, pipe}

    implicit val timeout = Timeout(1 seconds)
    import scala.concurrent.ExecutionContext.Implicits.global
    val myRef = self
    val myRanges = (data.aggregator ? GetMyPersonalRanges).mapTo[YourPersonalRanges].map(x => IamYourNewChild(x.personalRanges, myRef))
    pipe(myRanges) to master

    stay() using data.copy(parent = master)
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
    case Event(GiveHalf, data@ProcessData(subContractorsCurrent, details, iter, parent, master, aggregator)) =>
      iter.split() match {
        case Some((first, second)) =>
          sender() ! CheckHalf(second, details, master, aggregator)
          aggregator ! UpdatePersonalRange(first, details)
          parent ! UpdateSubcontractor(first, details)
          context.watch(sender())
          goto(stateName) using data.copy(subContractors = subContractorsCurrent + (sender() -> second),
            iterator = BigRangeIterator(first))

        case None => sender() ! Invalid
          stay()
      }

    case Event(CancelComputation, ProcessData(subContractors, _, _, _, _, aggregator)) =>
      subContractors.keys.foreach(_ ! CancelComputation)
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
      log.debug(s"\n\nI got range: $range about $details, but it is not processed anymore, so I ignore this message\n\n")
      stay()

    case Event(ImLeavingMsgToParent, data@ProcessData(subContractorsCurrent, _, _, _, _, _)) =>
      context.unwatch(sender())
      stay() using data.copy(subContractors = subContractorsCurrent - sender())

    case Event(Terminated(actor), data: ProcessData)
      if data.subContractors.contains(actor) =>
      val childRange = data.subContractors(actor)
      data.aggregator ! AddDiffRanges(childRange)
      log.info(s"\n\nchild died. His range was: $childRange\n\n")
      context.unwatch(actor)
      stay() using data.copy(subContractors = data.subContractors - actor)

    case Event(ComputedDiffs(diffRanges, updatedPersonal), data: ProcessData) =>
      val it = data.iterator.addRanges(diffRanges)
      data.parent ! UpdateSubcontractor(updatedPersonal, data.workDetails)
      log.info(s"\n\nMy new iterator is: $it\n\n")
      goto(stateName) using data.copy(iterator = it)

    case Event(UpdateSubcontractor(updatedRange, details), data: ProcessData) if details == data.workDetails =>
      stay() using data.copy(subContractors = data.subContractors + (sender() -> updatedRange))

    case msg => log.error(s"\n\n\n\n\n\n\n\n\n\n\nMY STATE: $stateName \n\n unhandled msg:$msg\n\n\n\n\n\n\n\n\n\n\n\n")
      stop()
  }

  initialize()

  private def leave(subContractors: Map[ActorRef, List[BigRange]], aggregator: ActorRef, parent: ActorRef) = {
    subContractors.keys.foreach(_ ! ImLeaving)
    aggregator ! ImLeaving
    parent ! ImLeavingMsgToParent
    goto(Idle) using Uninitialized
  }

  private def endNode(aggregator: ActorRef) = {
    aggregator ! PoisonPill
    goto(Idle) using Uninitialized
  }
}


object CoordinatorFSM extends Dehash {
  def props(alphabet: String, nrOfWorkers: Int = 4, queuePath: ActorPath): Props =
    Props(new CoordinatorFSM(alphabet, nrOfWorkers, queuePath))

  def nrOfIterations(maxStringSize: Int): BigInt = {
    (1 to maxStringSize).map(x => BigInt(math.pow(defaultAlphabet.length, x).toLong)).sum
  }
}
