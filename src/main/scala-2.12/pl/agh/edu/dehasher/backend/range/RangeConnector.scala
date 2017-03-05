package pl.agh.edu.dehasher.backend.range

import scala.language.implicitConversions


case class RangeConnector(ranges: List[BigRange] = List()) {
  def addRange(range: BigRange): RangeConnector = {
    val bigRange = BigRange(range.start, range.end)
    val after = ranges.find { it => it.start <= range.end && range.end <= it.end }
    val before = ranges.find { it => it.start <= range.start && range.start <= it.end }
    val adjacentRanges = List(before, Some(bigRange), after).flatten
    val merged = adjacentRanges.reduceLeft((before, after) => BigRange(before.start, after.end))
    val unredundant = ranges.filterNot(merged.contains)
    val newRanges = (merged :: unredundant).sortBy(_.start)
    RangeConnector(newRanges)
  }

}
