package pl.agh.edu.dehaser

import akka.actor.{Actor, ActorRef}

import scala.collection.mutable

// coordinate work on one node [computer]
class LocalCoordinator extends Actor with Dehash {
  private val slaves = mutable.Set.empty[ActorRef]
  private var rangeConnector: RangeConnector = _
  private var workDetails: WorkDetails = _
  private var rangeToCheck: BigRange = _
  private var iterator: BigRangeIterator = _

  // when task id completed[or cancelled] ask queue for more work

  // when nothing is calculated at the moment, then queue remembers
  // that this node is idle and send him workaviable signal,when the sth to calculate
  override def receive: Receive = {

    // I'm the root. Message form queue
    //todo maybe some client Ref? to know shold I report
    case DehashIt(hash, algo) =>

    // I'm only partial worker
    //message from other local node [parent in tree]
    case CheckHalf(range, hash, algo) =>
      rangeToCheck = range
      iterator = BigRangeIterable(range).iterator
      workDetails = WorkDetails(hash, algo)
      rangeConnector = RangeConnector()
      slaves.foreach(slave => if (iterator.hasNext) slave ! Check(iterator.next(), workDetails))

    case RangeChecked(range) =>
      // todo every ten ranges add send report upwards
      rangeConnector.addRange(range)
      if (iterator.hasNext) sender() ! Check(iterator.next(), workDetails)


    case FoundIt(crackedPass) =>
    // forward directly to master coordinator


    case GiveHalf =>
      val optionalRanges = iterator.split()
      if (optionalRanges.isDefined) {
        val (first, second) = optionalRanges.get
        iterator = BigRangeIterable(first).iterator
        sender() ! CheckHalf(second, workDetails.hash, workDetails.algo)
      }

  }

  override def preStart(): Unit = {
    // nr of wokrer actors approx equal to nr of cores [e.g. 4]
    (1 to 4).foreach(_ => slaves += context.actorOf(DehashWorker.props(defaultAlphabet)))
    //ask  queue  for work when joining cluster
    // if there is some task in queue then take it
  }

}


