package pl.agh.edu.dehaser

import akka.actor.{Actor, Props}
import pl.agh.edu.dehaser.messages._

import scala.annotation.tailrec

class DehashWorker(alphabet: String) extends Actor with Dehash {


  override def receive: Receive = {
    case Check(range, details@WorkDetails(hash, algo), hasher) =>
      val foundOption = range.map(x => getWord(x, alphabet, ""))
        .map(x => x -> hasher.createHash(x)).find(x => x._2.equals(hash)).map(_._1)
      foundOption match {
        case Some(crackedPass) =>
          sender ! FoundIt(crackedPass)
        case None => sender ! RangeChecked(range, details)
      }
    case WorkAvailable =>
      sender ! GiveMeRange
  }

  @tailrec
  private def getWord(iterator: BigInt, alphabet: String, accumulator: String): String = {
    if (iterator == 0) accumulator
    else {
      val modulo = (iterator - 1) % alphabet.length
      val newIterator = (iterator - 1) / alphabet.length
      val newChar = alphabet(modulo.toInt)
      val newAcc = newChar + accumulator
      getWord(newIterator, alphabet, newAcc)
    }
  }


}

object DehashWorker {
  def props(alphabet: String): Props = Props(new DehashWorker(alphabet))
}