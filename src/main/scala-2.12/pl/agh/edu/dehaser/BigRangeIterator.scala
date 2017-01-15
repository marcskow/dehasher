package pl.agh.edu.dehaser

import scala.collection.immutable.{Iterable, NumericRange}

case class BigRange(start: BigInt, end: BigInt)

// TODO: make immutable 
class BigRangeIterator(range: BigRange) extends Iterator[NumericRange[BigInt]] with Dehash {
  private var nextValue = range.start until (range.start + atomSize)

  override def hasNext: Boolean = range.end >= nextValue.start

  override def next: NumericRange[BigInt] = {
    val current = nextValue
    //noinspection ScalaUnnecessaryParentheses
    nextValue = (nextValue.end) until (atomSize + nextValue.end)
    current
  }

  def split(): Option[(BigRange, BigRange)] = {
    val length = range.end - nextValue.start + 1
    val atomLength = length / atomSize
    if (length < splitThreshold) None
    else {
      val half: BigInt = (atomLength / 2) * atomSize
      //noinspection ScalaUnnecessaryParentheses
      val split: BigInt = (nextValue.start) + half
      Some(BigRange(nextValue.start, split), BigRange(split, range.end + 1))
    }
  }
}

class BigRangeIterable(range: BigRange) extends Iterable[NumericRange[BigInt]] {
  override def iterator: BigRangeIterator = new BigRangeIterator(range)
}

object BigRangeIterable {
  def apply(range: BigRange): BigRangeIterable = new BigRangeIterable(range)
}
