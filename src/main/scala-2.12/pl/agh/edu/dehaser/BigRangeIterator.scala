package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange



case class BigRangeIterator(ranges: List[BigRange]) extends Dehash {
  val totalLength: BigInt = ranges.map(_.length).sum

  def addRanges(newRanges: List[BigRange]): BigRangeIterator =
    newRanges.foldLeft(this) { case (conn, curr) => conn.addRange(curr) }

  def addRange(range: BigRange): BigRangeIterator = {
    require(range.end >= range.start, s"range: $range")
    BigRangeIterator(ranges :+ range)
  }

  def next(): (Option[NumericRange[BigInt]], BigRangeIterator) = {
    ranges.headOption match {
      case Some(head) => if (head.start + atomSize < head.end) {
        val chunk = head.start until (head.start + atomSize)
        val truncatedHead = BigRange(head.start + atomSize, head.end)
        (Some(chunk), BigRangeIterator(truncatedHead :: ranges.tail))
      } else {
        val chunk = head.start until head.end
        (Some(chunk), BigRangeIterator(ranges.tail))
      }
      case None => (None, BigRangeIterator(Nil))
    }

  }


  def split(): Option[(List[BigRange], List[BigRange])] = {
    if (totalLength < splitThreshold) None
    else {
      val first = ranges.map(r => BigRange(r.start, r.start + r.length / 2))
      val second = ranges.map(r => BigRange(r.start + r.length / 2, r.end))
      Some(first, second)
    }
  }
}