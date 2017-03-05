package pl.agh.edu.dehasher.backend.range

import pl.agh.edu.dehasher.backend.Dehash

import scala.collection.immutable.NumericRange

case class BigRange(start: BigInt, end: BigInt) {
  val length: BigInt = end - start

  def contains(otherRange: BigRange): Boolean = otherRange.start >= start && otherRange.end <= end
}

case class BigRangeIterator(rangeOption: Option[BigRange]) extends Dehash {

  def next(): (Option[NumericRange[BigInt]], BigRangeIterator) = {
    rangeOption match {
      case Some(range) =>
        if (range.start + atomSize < range.end) {
          val chunk = range.start until (range.start + atomSize)
          val truncated = BigRange(range.start + atomSize, range.end)
          (Some(chunk), BigRangeIterator(Some(truncated)))
        } else {
          val chunk = range.start until range.end
          (Some(chunk), BigRangeIterator(None))
        }
      case None => (None, BigRangeIterator(None))
    }

  }


}