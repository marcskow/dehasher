package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange

trait BigRangeTrait {
  def length: BigInt

  def contains(otherRange: BigRange): Boolean

  def start: BigInt

  def end: BigInt
}

case class BigRange(start: BigInt, end: BigInt) extends BigRangeTrait {
  val length: BigInt = end - start

  def contains(otherRange: BigRange): Boolean = otherRange.start >= start && otherRange.end <= end
}

object RangeImplicit {

  implicit class NumericRangeToBigRange(range: NumericRange[BigInt]) extends BigRangeTrait {
    override def length: BigInt = range.length

    override def contains(otherRange: BigRange): Boolean = otherRange.start >= start && otherRange.end <= end

    override def start: BigInt = range.start

    override def end: BigInt = range.length
  }


}
