package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange
import scala.collection.mutable.ListBuffer


class RangeConnector {
  var ranges = new ListBuffer[NumericRange[Long]]

  def addRange(range: NumericRange[Long]) {
    val after = ranges.find(_.start == range.end + 1)
    val before = ranges.find(_.end == range.start + 1)

    val a = after.map(x => range.start to x.end)
    val b = before.map(x => x.start to range.end)
    val merged = List(b, a).flatten.reduceLeft((before, after) => before.start to after.end)
    val possList = List(before, after).flatten

    ranges --= possList

    ranges += merged

  }
}