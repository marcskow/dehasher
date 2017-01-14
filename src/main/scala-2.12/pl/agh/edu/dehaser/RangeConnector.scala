package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange
import scala.collection.mutable.ListBuffer


class RangeConnector {
  private var _ranges = new ListBuffer[NumericRange[BigInt]]

  def addRange(range: NumericRange[BigInt]): Unit = {
    val after = _ranges.find(_.start == range.end)
    val before = _ranges.find(_.end == range.start)

    val adjactRanges = List(before, Some(range), after).flatten
    val merged = adjactRanges.reduceLeftOption((before, after) => before.start until after.end)
    val possiblyRedundant = List(before, after).flatten

    _ranges --= possiblyRedundant

    _ranges ++= merged
    _ranges = _ranges.sortBy(_.start)
  }

  def ranges: List[NumericRange[BigInt]] = _ranges.toList
}

object RangeConnector {
  def apply(): RangeConnector = new RangeConnector()
}