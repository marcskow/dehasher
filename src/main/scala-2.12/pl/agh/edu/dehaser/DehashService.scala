package pl.agh.edu.dehaser

import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.routing.FromConfig
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, DelayOverflowStrategy, KillSwitches}
import akka.util.Timeout
import akka.{Done, NotUsed}

import scala.collection.immutable.NumericRange
import scala.concurrent.Future
import scala.concurrent.duration._

object DehashService {
  def props: Props = Props(new DehashService())

}

class DehashService(atomSize: Int = 10000, maxNrOfchars: Int = 7) extends Actor with Dehash {
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

      implicit val askTimeout = Timeout(5.seconds)
      implicit val materializer = ActorMaterializer()


      val countingSrc: Source[Int, NotUsed] = Source(Stream.from(1)).delay(1.second, DelayOverflowStrategy.backpressure)
      val source: Source[NumericRange[BigInt], NotUsed] = Source[NumericRange[BigInt]](BigRangeIterable(1, nrOfIterations(maxNrOfchars), atomSize))

      val lastSnk: Sink[Any, Future[Done]] = Sink.ignore

      //doSomethingElse()
      //      via(Flow[UserID].mapAsync(maxLookupCount)(concurrentDBLookup))
      //        .to(Sink.foreach[FullName](println))

      val routing: Flow[NumericRange[BigInt], Any, NotUsed] = Flow[NumericRange[BigInt]].mapAsync(parallelism = 1000)(range => workerRouter ? Check(range, hash, algo))
      val founder: Flow[Any, Any, NotUsed] = Flow[Any].alsoTo(Flow[Any].filter(_.isInstanceOf[FoundIt]).take(1).to(Sink.foreach(println)))
      val stopper: Sink[Any, NotUsed] = Flow[Any].takeWhile(_.isInstanceOf[RangeChecked]).to(Sink.foreach(println))
      val (killSwitch, last) = source
        .viaMat(KillSwitches.single)(Keep.right)
        .via(routing)
        .via(founder)
        .toMat(stopper)(Keep.both)
        .run()
    //      killSwitch.shutdown()

    //      Await.result(last, 1.second) //shouldBe 2


    //          source.mapAsync(parallelism = 1000) { range => workerRouter ? Check(range, hash, algo) }
    //        .map()

    //        runWith(Sink.ignore)


    // todo cancel computation
    // todo see progress
  }

  def nrOfIterations(maxStringSize: Int): BigInt = {
    (1 to maxStringSize).map(x => math.pow(alphabetSize, x).toLong).sum
  }


}




