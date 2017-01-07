package pl.agh.edu.dehaser

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor6}

import scala.collection.immutable.NumericRange.Inclusive

class WorkerTest extends TestKit(ActorSystem("NodeActorSpec")) with ImplicitSender
  with FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks with Dehash with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  describe("Worker test") {


    it("Worker should send FoundIt message") {

      val a_z = "abcdefghijklmnopqrstuvwxyz"
      //      "MD5" "SHA-1" "SHA-256"
      val testRange = stringToNumber("test", a_z)
      val tezzRange = stringToNumber("tezz", a_z)
      val dupaRange = stringToNumber("dupa", a_z)
      val aaaaaRange = stringToNumber("aaaaa", a_z)
      val xyRange = stringToNumber("xy", a_z)
      val xyaaRange = stringToNumber("xyaa", a_z)
      val hashes: TableFor6[String, String, String, String, Inclusive[Long], Any] =
        Table(
          ("hash", "dehashed", "algo", "alphabet", "range", "message"), // First tuple defines column names
          ("38164fbd17603d73f696b8b4d72664d735bb6a7c88577687fd2ae33fd6964153", "AB", "SHA-256", "ABC", 1L to 1000L, FoundIt("AB")), // Subsequent tuples define the data
          ("f62d6f44dd33c275a0656e11ef9fd793bebd92ce68d2f23e69bb279ef74e3ac6", "teww", "SHA-256", a_z, testRange to tezzRange, FoundIt("teww")),
          ("2254139645ffdd350372a68e0cc4271731019751", "dupsko", "SHA-1", a_z, dupaRange to aaaaaRange, RangeChecked(dupaRange to aaaaaRange)),
          ("d16fb36f0911f878998c136191af705e", "xyz", "MD5", a_z, xyRange to xyaaRange, FoundIt("xyz"))
        )


      forAll(hashes) { (hash, dehashed, algo, alphabet, range, message) =>
        Given(s"alphabet: $alphabet  \n hash: $hash \n algo: $algo \n range: $range ")
        val worker = system.actorOf(Props(classOf[Worker], alphabet))

        When("Check message is send")
        worker ! Check(range, hash, algo)

        Then(s"Worker should send $message \n")
        expectMsg(message)
      }

    }
  }
}