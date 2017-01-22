package pl.agh.edu.dehaser

trait Dehash {
  // TODO: move these settings to config file
  val defaultAlphabet: String =
    """ !\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"""

  val atomSize = 500000
  val splitThreshold: Int = 20 * atomSize
  val maxNrOfChars = 7

  def stringToNumber(word: String, alphabet: String): BigInt = {
    word.reverseIterator.map(char => alphabet.indexOf(char)).zipWithIndex.
      map { case (char, index) => (char + 1) * Math.pow(alphabet.length, index) }.sum.toLong
  }

}
