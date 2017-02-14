package pl.agh.edu.dehaser

import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}
import org.scalatest.{FunSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable.NumericRange


class RangeConnectorTest extends FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks {

  describe("RangeConncetor ") {
    it("should add and merge non-overlapping Ranges in addRange method") {
      Given("clean range")


      val ranges: TableFor2[NumericRange[BigInt], List[NumericRange[BigInt]]] = Table(
        ("range", "result"),
        (BigInt(1) until 1001, (BigInt(1) until 1001) :: Nil),
        (BigInt(3001) until 10001, (BigInt(1) until 1001) :: (BigInt(3001) until 10001) :: Nil),
        (BigInt(1001) until 2001, (BigInt(1) until 2001) :: (BigInt(3001) until 10001) :: Nil),
        (BigInt(2001) until 3001, (BigInt(1) until 10001) :: Nil)
      )
      testRanges(ranges)

    }


    it("should tell whether it contains range in question") {
      val connector = RangeConnector(List(BigRange(1, 10001)))
      assert(connector.contains(BigRange(BigInt(1), 10001)))
      assert(connector.contains(BigRange(BigInt(1000), 10001)))
      assert(connector.contains(BigRange(BigInt(1000), 9001)))
      assert(!connector.contains(BigRange(BigInt(1000), 11001)))
    }

    it("should add and merge possibly overlapping Ranges in addRange method") {
      Given("clean range")


      val ranges: TableFor2[NumericRange[BigInt], List[NumericRange[BigInt]]] = Table(
        ("range", "result"),
        (BigInt(1) until 2201, (BigInt(1) until 2201) :: Nil),
        (BigInt(2801) until 10001, (BigInt(1) until 2201) :: (BigInt(2801) until 10001) :: Nil),
        (BigInt(2601) until 2701, (BigInt(1) until 2201) :: (BigInt(2601) until 2701) :: (BigInt(2801) until 10001) :: Nil),
        (BigInt(2001) until 3001, (BigInt(1) until 10001) :: Nil)
      )

      val ranges2: TableFor2[NumericRange[BigInt], List[NumericRange[BigInt]]] = Table(
        ("range", "result"),
        (BigInt(1) until 2201, (BigInt(1) until 2201) :: Nil),
        (BigInt(3501) until 10001, (BigInt(1) until 2201) :: (BigInt(3501) until 10001) :: Nil),
        (BigInt(2601) until 2701, (BigInt(1) until 2201) :: (BigInt(2601) until 2701) :: (BigInt(3501) until 10001) :: Nil),
        (BigInt(2001) until 3001, (BigInt(1) until 3001) :: (BigInt(3501) until 10001) :: Nil),
        (BigInt(2001) until 3001, (BigInt(1) until 3001) :: (BigInt(3501) until 10001) :: Nil)
      )

      testRanges(ranges)
      testRanges(ranges2)

    }

  }

  def testRanges(ranges: TableFor2[NumericRange[BigInt], List[NumericRange[BigInt]]]): Unit = {
    var connector = RangeConnector()
    forAll(ranges) { (range, result) =>
      When(s"Adding $range to existing connector")
      connector = connector.addRange(range)

      Then(s"connceted ranges should be: $result")
      assert(connector.ranges.length === result.length, "[length]ranges:" + connector.ranges)
      connector.ranges.zip(result).foreach { case (x, y) => assert(x.start === y.start, "[start]ranges:" + connector.ranges) }
      connector.ranges.zip(result).foreach { case (x, y) => assert(x.end === y.end, "ranges[end]:" + connector.ranges) }

    }
  }
}



