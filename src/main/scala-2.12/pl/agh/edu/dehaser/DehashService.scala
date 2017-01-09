package pl.agh.edu.dehaser

import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.routing.FromConfig
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout

import scala.collection.immutable.NumericRange
import scala.concurrent.duration._

object DehashService {
  def props: Props = Props(new DehashService())

}

class DehashService(atomSize: Int = 1000, maxNrOfchars: Int = 7) extends Actor with Dehash {
  val alphabet: String = defaultAlphabet

  val alphabetSize: Int = alphabet.length
  val nrOfWords: BigInt = nrOfIterations(maxNrOfchars)
  // TODO: maybe props(empty) is sufficient?
  private val workerRouter = context.actorOf(FromConfig.props(DehashWorker.props(alphabet)),
    name = "workerRouter")

  override def receive: Receive = {
    case DehashIt(hash, algo) =>
      val replyTo = sender()
      val aggregator = context.actorOf(DehashAggregator.props(replyTo, hash, algo, atomSize, maxNrOfchars, alphabet))

      implicit val askTimeout = Timeout(5 seconds)
      implicit val materializer = ActorMaterializer()

      val source = Source[NumericRange[BigInt]](BigRangeIterable(1, nrOfIterations(maxNrOfchars), atomSize))


      source.mapAsync(parallelism = 100) { range => workerRouter ? Check(range, hash, algo) }.runWith(Sink.ignore)


    // todo cancel computation
    // todo see progress
  }

  def nrOfIterations(maxStringSize: Int): BigInt = {
    (1 to maxStringSize).map(x => math.pow(alphabetSize, x).toLong).sum
  }


}




