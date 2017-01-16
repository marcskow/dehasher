package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange


class RangeConnector(val ranges: List[BigRange]) {

  def addRange(range: NumericRange[BigInt]): RangeConnector = {
    val bigRange = BigRange(range.start, range.end)
    val after = ranges.find(_.start == range.end)
    val before = ranges.find(_.end == range.start)
    val adjactRanges = List(before, Some(bigRange), after).flatten
    val merged = adjactRanges.reduceLeftOption((before, after) => BigRange(before.start, after.end))
    val possiblyRedundant = List(before, after).flatten
    // TODO: eliminate  overlapping ranges
    val unredundat = ranges.filterNot(possiblyRedundant.contains(_))
    val newRanges = (unredundat ++ merged).sortBy(_.start)
    RangeConnector(newRanges)
  }


  def contains(range: BigRange): Boolean = ranges.exists(x => x.start <= range.start && x.end >= range.end)

}

object RangeConnector {
  def apply(ranges: List[BigRange] = List()): RangeConnector = new RangeConnector(ranges)
}