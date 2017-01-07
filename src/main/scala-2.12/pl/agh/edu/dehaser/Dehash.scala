package pl.agh.edu.dehaser

trait Dehash {

  protected def stringToNumber(word: String, alphabet: String): Long = {
    word.reverseIterator.map(char => alphabet.indexOf(char)).zipWithIndex.
      map { case (char, index) => (char + 1) * Math.pow(alphabet.length, index) }.sum.toLong
  }
}
