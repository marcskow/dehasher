package pl.agh.edu.dehaser

import akka.actor.{Actor, Props}
import akka.routing.FromConfig

object DehashService {

  def props: Props = Props(new DehashService())
}

class DehashService(atomSize: Int = 1000, maxNrOfchars: Int = 6, // maxNrOfchars: Int = 7,
                    alphabet: String = "abcdefghijklmnopqrstuvwxyz") extends Actor {
  //                    alphabet: String =  !\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~""") extends Actor {
  // TODO: restrore normal alphabet

  val alphabetSize: Int = alphabet.length
  val nrOfWords: Long = nrOfIterations(maxNrOfchars)
  private val workerRouter = context.actorOf(FromConfig.props(Props[DehashWorker]),
    name = "workerRouter")

  override def receive: Receive = {
    case DehashIt(hash, algo) =>
      val replyTo = sender()
      val aggregator = context.actorOf(DehashAggregator.props(replyTo, hash, algo, atomSize, maxNrOfchars, alphabet))

      // todo create own arbitrarly big range:  java.lang.IllegalArgumentException: More than Int.MaxValue elements.

      val wholeRange = 1L to nrOfWords
      wholeRange.grouped(atomSize).map(x => x.head to x.last).foreach(range =>
        workerRouter.tell(Check(range, hash, algo), aggregator))

    // todo cancel computation
    // todo see progress
  }

  // TODO: maybe use BIgInt
  def nrOfIterations(maxStringSize: Int): Long = {
    (1 to maxStringSize).map(x => math.pow(alphabetSize, x).toLong).sum
  }

}




