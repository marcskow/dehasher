package pl.agh.edu.dehaser

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor6}

import scala.collection.immutable.NumericRange.Inclusive

class DehashWorkerTest extends TestKit(ActorSystem("NodeActorSpec")) with ImplicitSender
  with FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks with Dehash with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  describe("Worker test") {


    it("Worker should send FoundIt or RangeChecked message") {

      val a_z = "abcdefghijklmnopqrstuvwxyz"
      val testRange = stringToNumber("test", a_z)
      val tezzRange = stringToNumber("tezz", a_z)
      val lalaRange = stringToNumber("lala", a_z)
      val aaaaaRange = stringToNumber("aaaaa", a_z)
      val xyRange = stringToNumber("xy", a_z)
      val xyaaRange = stringToNumber("xyaa", a_z)
      val hashes: TableFor6[String, String, String, String, Inclusive[BigInt], Any] =
        Table(
          ("hash", "dehashed", "algo", "alphabet", "range", "message"), // First tuple defines column names
          ("38164fbd17603d73f696b8b4d72664d735bb6a7c88577687fd2ae33fd6964153", "AB", "SHA-256", "ABC", BigInt(1) to 1000, FoundIt("AB")), // Subsequent tuples define the data
          ("f62d6f44dd33c275a0656e11ef9fd793bebd92ce68d2f23e69bb279ef74e3ac6", "teww", "SHA-256", a_z, testRange to tezzRange, FoundIt("teww")),
          ("0efdc6151ea756af355d3fc133f4e8b2145c70a2", "alicja", "SHA-1", a_z, lalaRange to aaaaaRange, RangeChecked(lalaRange to aaaaaRange, WorkDetails("0efdc6151ea756af355d3fc133f4e8b2145c70a2", "SHA-1"))),
          ("d16fb36f0911f878998c136191af705e", "xyz", "MD5", a_z, xyRange to xyaaRange, FoundIt("xyz"))
        )


      forAll(hashes) { (hash, dehashed, algo, alphabet, range, message) =>
        Given(s"alphabet: $alphabet  \n hash: $hash \n algo: $algo \n range: $range ")
        val worker = system.actorOf(DehashWorker.props(alphabet))

        When("Check message is send")
        worker ! Check(range, WorkDetails(hash, algo))

        Then(s"Worker should send $message \n")
        expectMsg(message)
      }

    }
  }
}