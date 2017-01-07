package pl.agh.edu.dehaser

import java.math.BigInteger
import java.security.MessageDigest

import akka.actor.Actor

import scala.annotation.tailrec

//  todo create worker, with range that is recursively consumend unless there comes terminate or split message
class Worker(alphabet: String) extends Actor {


  override def receive: Receive = {
    case Check(range, hash, algo) =>
      val foundOption = range.map(x => getWord(x, alphabet, ""))
        .map(x => x -> hasher(x, algo)).find(x => x._2.equals(hash)).map(_._1)
      foundOption match {
        case Some(crackedPass) => sender ! FoundIt(crackedPass)
        case None => sender ! RangeChecked(range)
      }
  }

  @tailrec
  private def getWord(iterator: Long, alphabet: String, accumulator: String): String = {
    if (iterator == 0) accumulator
    else {
      val modulo = (iterator - 1) % alphabet.length
      val newIterator = (iterator - 1) / alphabet.length
      val newChar = alphabet(modulo.toInt)
      val newAcc = newChar + accumulator
      getWord(newIterator, alphabet, newAcc)
    }
  }

  private def hasher(input: String, algo: String): String = {
    val md = MessageDigest.getInstance(algo)
    md.update(input.getBytes("UTF-8"))
    val bytes = md.digest
    String.format("%064x", new BigInteger(1, bytes))
  }


}