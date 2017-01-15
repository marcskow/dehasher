package pl.agh.edu.dehaser

import akka.actor.{Actor, ActorRef, Props}

import scala.collection.mutable

// todo idea: implement sth like bittorrent incentive mechanism
// coordinate work on one node [computer]
class LocalCoordinator(alphabet: String) extends Actor {
  private val slaves = mutable.Set.empty[ActorRef]
  private val subContractors = mutable.Set[ActorRef]()
  private val queue = context.actorSelection("/user/queue")
  private var reportAggregator: ActorRef = _
  private var masterCoordinator: ActorRef = _
  private var rangeConnector: RangeConnector = _
  private var workDetails: WorkDetails = _
  private var rangeToCheck: BigRange = _
  private var iterator: BigRangeIterator = _
  private var client: ActorRef = _


  // when task id completed[or cancelled] ask queue for more work

  // when nothing is calculated at the moment, then queue remembers
  // that this node is idle and send him workaviable signal,when the sth to calculate
  override def receive: Receive = {

    // I'm the root.
    case DehashIt(hash, algo, originalSender) =>
      masterCoordinator = self
      client = originalSender
      rangeConnector = RangeConnector()


    // I'm only partial worker
    //message from other local node [parent in tree]
    case CheckHalf(range, details, master) =>
      rangeToCheck = range
      iterator = BigRangeIterable(range).iterator
      workDetails = details
      rangeConnector = RangeConnector()
      reportAggregator = sender()
      masterCoordinator = master
      slaves.foreach(slave => if (iterator.hasNext) slave ! Check(iterator.next(), workDetails))

    case RangeChecked(range) =>
      // todo every ten ranges add send report upwards
      rangeConnector.addRange(range)
      if (iterator.hasNext) sender() ! Check(iterator.next(), workDetails)
      else reportAggregator ! DidMyWork(rangeToCheck, workDetails)
    // TODO: else send UpdateProgress(ranges) upwards
    // todo cancel computation
    //    // todo see progress

    case FoundIt(crackedPass) =>
      if (masterCoordinator != self) masterCoordinator ! FoundIt(crackedPass)
      client ! Cracked(crackedPass)
      reinitialize()

    case CancelComputaion =>
      reinitialize()

    // TODO: what if someone takes range and message will be lost, or someone will disconnect form cluster before ending its range?
    //todo maybe keep track who took range in map (range -> actor)and watch it's lifecycle[Akka]?

    case GiveHalf =>
      val optionalRanges = iterator.split()
      if (optionalRanges.isDefined) {
        val (first, second) = optionalRanges.get
        iterator = BigRangeIterable(first).iterator
        subContractors += sender()
        sender() ! CheckHalf(second, workDetails, masterCoordinator)
      }
      else sender() ! Invalid

  }

  private def reinitialize(): Unit = {
    subContractors.foreach(_ ! CancelComputaion)
    subContractors.clear()
    //call queue
  }

  override def preStart(): Unit = {
    // nr of wokrer actors approx equal to nr of cores [e.g. 4]
    (1 to 4).foreach(_ => slaves += context.actorOf(DehashWorker.props(alphabet)))
    //ask  queue  for work when joining cluster
    // if there is some task in queue then take it
  }
}

object LocalCoordinator {
  def props(alphabet: String) = Props(new LocalCoordinator(alphabet))
}


