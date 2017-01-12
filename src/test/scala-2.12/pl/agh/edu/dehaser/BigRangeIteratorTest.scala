package pl.agh.edu.dehaser

import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}
import org.scalatest.{FunSpecLike, GivenWhenThen, Matchers}

class BigRangeIteratorTest extends FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks with Dehash {

  describe("BigRangeIterator ") {
    it("should split its Range if it is not smaller than threshold ") {
      val connector = RangeConnector()

      val ranges: TableFor3[BigRange, Int, Option[(BigRange, BigRange)]] = Table(
        ("initial range", "nr of iterations", "splitted range"),
        (BigRange(1, 100 * atomSize), 10, Some(BigRange(10 * atomSize + 1, 55 * atomSize + 1), BigRange(55 * atomSize + 1, 100 * atomSize + 1))),
        (BigRange(1, 100 * atomSize + 500), 10, Some(BigRange(10 * atomSize + 1, 55 * atomSize + 1), BigRange(55 * atomSize + 1, 100 * atomSize + 500 + 1))),
        (BigRange(1, 100 * atomSize + 500), 0, Some(BigRange(1, 50 * atomSize + 1), BigRange(50 * atomSize + 1, 100 * atomSize + 500 + 1))),
        (BigRange(1, 100 * atomSize + 500), 100 - (splitThreshold / atomSize) + 1, None))

      forAll(ranges) { (initial, iterations, splitted) =>

        Given(s"Iterator with initial range: $initial and after $iterations iterations; [atomsize= $atomSize]")
        val iter = BigRangeIterable(initial).iterator
        (1 to iterations).foreach(_ => iter.next())
        When("split is called")
        val result = iter.split()

        Then(s"splitted halves should be: $splitted")
        assert(result === splitted)

      }
    }
  }
}

