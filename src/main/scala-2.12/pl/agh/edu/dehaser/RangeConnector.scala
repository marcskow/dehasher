package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange
import scala.collection.mutable.ListBuffer


class RangeConnector {
  private var _ranges = new ListBuffer[BigRange]

  def addRange(range: NumericRange[BigInt]): Unit = {
    val bigRange = BigRange(range.start, range.end)
    val after = _ranges.find(_.start == range.end)
    val before = _ranges.find(_.end == range.start)
    val adjactRanges = List(before, Some(bigRange), after).flatten
    val merged = adjactRanges.reduceLeftOption((before, after) => BigRange(before.start, after.end))
    val possiblyRedundant = List(before, after).flatten
    // TODO: eliminate  overlapping ranges 
    _ranges --= possiblyRedundant

    _ranges ++= merged
    _ranges = _ranges.sortBy(_.start)
  }

  def ranges: List[BigRange] = _ranges.toList
}

object RangeConnector {
  def apply(): RangeConnector = new RangeConnector()
}