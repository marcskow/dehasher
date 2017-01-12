package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange
import scala.collection.mutable.ListBuffer


class RangeConnector {
  private var _ranges = new ListBuffer[NumericRange[BigInt]]
  _ranges += (BigInt(1) until BigInt(1))

  def addRange(range: NumericRange[BigInt]) {
    val after = _ranges.find(_.start == range.end + 1)
    val before = _ranges.find(_.end == range.start + 1)

    val a = after.map(x => range.start to x.end)
    val b = before.map(x => x.start to range.end)
    val merged = List(b, a).flatten.reduceLeftOption((before, after) => before.start to after.end)
    val possList = List(before, after).flatten

    _ranges --= possList

    merged.foreach(x => _ranges += x)

  }

  def ranges: List[NumericRange[BigInt]] = _ranges.toList
}

object RangeConnector {
  def apply(): RangeConnector = new RangeConnector()
}