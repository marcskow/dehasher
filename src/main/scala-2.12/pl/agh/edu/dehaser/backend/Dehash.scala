package pl.agh.edu.dehaser.backend

import pl.agh.edu.dehaser.backend.range.BigRange

import scala.collection.immutable.NumericRange
import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

trait Dehash {
  // TODO: move these settings to config file
  // TODO: testing value
  val defaultAlphabet: String =
  """abcdefghijklmnopqrstuvwxyz"""

  val atomSize = 50000
  val splitThreshold: Int = 20 * atomSize
  val maxNrOfChars = 6

  // TODO: testing value
  val reloadTime: FiniteDuration = 1 seconds

  def stringToNumber(word: String, alphabet: String): BigInt = {
    word.reverseIterator.map(char => alphabet.indexOf(char)).zipWithIndex.
      map { case (char, index) => (char + 1) * Math.pow(alphabet.length, index) }.sum.toLong
  }

  implicit def rangeToBigRange(range: NumericRange[BigInt]): BigRange = BigRange(range.start, range.end)

}
