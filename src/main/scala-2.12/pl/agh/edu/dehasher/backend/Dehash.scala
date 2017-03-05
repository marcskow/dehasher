package pl.agh.edu.dehasher.backend

import pl.agh.edu.dehasher.backend.range.BigRange

import scala.collection.immutable.NumericRange
import scala.language.{implicitConversions, postfixOps}

trait Dehash {

  val atomSize = 50000
  val splitThreshold: Int = 20 * atomSize


  def stringToNumber(word: String, alphabet: String): BigInt = {
    word.reverseIterator.map(char => alphabet.indexOf(char)).zipWithIndex.
      map { case (char, index) => (char + 1) * Math.pow(alphabet.length, index) }.sum.toLong
  }

  implicit def rangeToBigRange(range: NumericRange[BigInt]): BigRange = BigRange(range.start, range.end)

}
