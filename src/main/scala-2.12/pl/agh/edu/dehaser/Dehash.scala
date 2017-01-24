package pl.agh.edu.dehaser

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Commons
import com.typesafe.sslconfig.util.ConfigLoader

trait Dehash {
  // TODO: move these settings to config file
  val defaultAlphabet: String =
    """ !\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"""

//  val config = ConfigLoader.

  val atomSize = 50000
  val splitThreshold: Int = 20 * atomSize
  val maxNrOfChars = 5

  def stringToNumber(word: String, alphabet: String): BigInt = {
    word.reverseIterator.map(char => alphabet.indexOf(char)).zipWithIndex.
      map { case (char, index) => (char + 1) * Math.pow(alphabet.length, index) }.sum.toLong
  }

}
