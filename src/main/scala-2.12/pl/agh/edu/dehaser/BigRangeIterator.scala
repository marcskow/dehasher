package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange

case class BigRange(start: BigInt, end: BigInt)

// TODO: make immutable 
class BigRangeIterator(range: BigRange) extends Dehash {
  //  require(range.end >= range.start, s"range: $range")

  private val nextValue = if (range.end > range.start)
    Some(range.start until (range.start + atomSize))
  else None

  // TODO: may produce neagative ranges (start > end)
  def next: (Option[NumericRange[BigInt]], BigRangeIterator) = {
    val nextIt = BigRangeIterator(BigRange(range.start + atomSize, range.end))
    (nextValue, nextIt)
  }

  def split(): Option[(BigRange, BigRange)] = {
    val length = range.end - range.start + 1
    val atomLength = length / atomSize
    if (length < splitThreshold) None
    else {
      val half: BigInt = (atomLength / 2) * atomSize
      //noinspection ScalaUnnecessaryParentheses
      val splitPoint: BigInt = (range.start) + half
      Some(BigRange(range.start, splitPoint), BigRange(splitPoint, range.end + 1))
    }
  }
}

object BigRangeIterator {
  def apply(range: BigRange): BigRangeIterator = new BigRangeIterator(range)
}

