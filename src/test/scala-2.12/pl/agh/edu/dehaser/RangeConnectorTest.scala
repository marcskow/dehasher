package pl.agh.edu.dehaser

import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}
import org.scalatest.{FunSpecLike, GivenWhenThen, Matchers}

import scala.collection.immutable.NumericRange


class RangeConnectorTest extends FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks {

  describe("RangeConncetor ") {
    it("should add and merge Range ion addRange method") {
      Given("clean range")
      val connector = RangeConnector()

      val ranges: TableFor2[NumericRange[BigInt], List[NumericRange[BigInt]]] = Table(
        ("range", "result"),
        (BigInt(1) until 1001, (BigInt(1) until 1001) :: Nil),
        (BigInt(3001) until 10001, (BigInt(1) until 1001) :: (BigInt(3001) until 10001) :: Nil),
        (BigInt(1001) until 2001, (BigInt(1) until 2001) :: (BigInt(3001) until 10001) :: Nil),
        (BigInt(2001) until 3001, (BigInt(1) until 10001) :: Nil) //,
        //        (BigInt(2001) until 3001, (BigInt(1) until 10001) :: Nil)
      )


      forAll(ranges) { (range, result) =>
        When(s"Adding $range to existing connector")
        connector.addRange(range)
        Then(s"connceted ranges should be: $result")
        assert(connector.ranges.length === result.length, "[length]ranges:" + connector.ranges)
        connector.ranges.zip(result).foreach { case (x, y) => assert(x.start === y.start, "[start]ranges:" + connector.ranges) }
        connector.ranges.zip(result).foreach { case (x, y) => assert(x.end === y.end, "ranges[end]:" + connector.ranges) }

      }
    }
  }


}
