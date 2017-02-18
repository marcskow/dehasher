package pl.agh.edu.dehaser

import scala.language.implicitConversions


case class RangeConnector(ranges: List[BigRange] = List()) {


  def merge(rangeConnector: RangeConnector): RangeConnector = {
    rangeConnector.ranges.foldLeft(this) { case (conn, curr) => conn.addRange(curr) }
  }

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

  def contains(rangesToCheck: List[BigRange]): Boolean = rangesToCheck.forall(contains)

  private def contains(range: BigRange): Boolean = ranges.exists(x => x.start <= range.start && x.end >= range.end)

  def diff(assignedRanges: List[BigRange]): List[BigRange] = assignedRanges.flatMap(diff)

  private def diff(assigned: BigRange): List[BigRange] =
    ranges.map(x => intersection(x, assigned)).filter(x => x.length > 0)

  // todo make right version
  private def intersection(bigRange1: BigRange, bigRange2: BigRange) =
    BigRange(bigRange1.start.max(bigRange2.start), bigRange1.end.min(bigRange2.end))

}
