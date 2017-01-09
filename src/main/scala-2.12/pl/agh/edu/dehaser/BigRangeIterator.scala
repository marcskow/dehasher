package pl.agh.edu.dehaser

import scala.collection.immutable.{Iterable, NumericRange}

class BigRangeIterator(start: BigInt, end: BigInt, step: Int) extends Iterator[NumericRange[BigInt]] {
  private var nextValue = start until (start + step)

  def hasNext: Boolean = end + 2 > nextValue.end

  def next: NumericRange[BigInt] = {
    val current = nextValue
    //noinspection ScalaUnnecessaryParentheses
    nextValue = (nextValue.end) until (step + nextValue.end)
    current
  }
}

class BigRangeIterable(start: BigInt, end: BigInt, step: Int) extends Iterable[NumericRange[BigInt]] {
  //  def apply(start: BigInt, end: BigInt, step: Int = 1): BigRangeIterator = //new BigRangeIterator(start, end, step)
  override def iterator: Iterator[NumericRange[BigInt]] = new BigRangeIterator(start, end, step)
}

object BigRangeIterable {
  def apply(start: BigInt, end: BigInt, step: Int): BigRangeIterable = new BigRangeIterable(start, end, step)
}
