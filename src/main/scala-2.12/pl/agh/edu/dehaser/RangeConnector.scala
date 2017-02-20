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

  def diff(declaredRanges: List[BigRange]): List[BigRange] = {
    val uncomputed = for (
      declared <- declaredRanges;
      negated <- negated(declaredRanges)
    ) yield intersection(declared, negated)
    uncomputed.filter(_.length > 0).sortBy(_.start)
  }


  private def intersection(bigRange1: BigRange, bigRange2: BigRange): BigRange =
    BigRange(bigRange1.start.max(bigRange2.start), bigRange1.end.min(bigRange2.end))

  private def negated(declaredRanges: List[BigRange]) = {
    val starting = BigRange(declaredRanges.head.start, ranges.head.start)
    val ending = BigRange(ranges.last.end, declaredRanges.last.end)
    val holes = ranges match {
      case _ :: Nil => Nil
      case manyElements => negate(manyElements)
    }
    holes ++ List(starting, ending).filter(_.length > 0)
  }

  private def negate(listOfRanges: List[BigRange]) =
    listOfRanges.sliding(2, 1).map { case List(fst, snd) => BigRange(fst.end, snd.start) }.toList
}
