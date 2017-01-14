package pl.agh.edu.dehaser

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.concurrent.duration._

//noinspection TypeAnnotation
class LocalCoordinatorTest extends TestKit(ActorSystem("NodeActorSpec")) with ImplicitSender
  with FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks with Dehash with BeforeAndAfterAll {

  val a_z = "abcdefghijklmnopqrstuvwxyz"
  val testRange = stringToNumber("test", a_z)
  val tezzRange = stringToNumber("tezz", a_z)
  val dupaRange = stringToNumber("dupa", a_z)
  val aaaaaRange = stringToNumber("aaaaa", a_z)
  val aaaaaaaRange = stringToNumber("aaaaaaa", a_z)
  val aaRange = stringToNumber("aa", a_z)
  val xyRange = stringToNumber("xy", a_z)

  val xyaaRange = stringToNumber("xyaa", a_z)


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("Local Coordinator test") {


    it("Coordinator should send DidMyWork message") {
      val local = system.actorOf(LocalCoordinator.props(a_z))
      val hashes =
        Table(
          ("hash", "dehashed", "algo", "range"), // First tuple defines column names
          ("2254139645ffdd350372a68e0cc4271731019751", "dupsko", "SHA-1", BigRange(aaRange, dupaRange)) //,
        )

      forAll(hashes) { (hash, dehashed, algo, range) =>
        Given(s"alphabet: $a_z  \n hash: $hash \n algo: $algo \n range: $range ")

        When("Check message is send")
        local ! CheckHalf(range, WorkDetails(hash, algo))

        Then(s"DidMyWork message should sent \n")
        expectMsgType[DidMyWork]
      }
    }

    it("Coordinator should find solution") {
      val local = system.actorOf(LocalCoordinator.props(a_z))
      val hashes =
        Table(
          ("hash", "dehashed", "algo", "range"), // First tuple defines column names
          ("d16fb36f0911f878998c136191af705e", "xyz", "MD5", BigRange(xyRange, xyaaRange)),
          ("2254139645ffdd350372a68e0cc4271731019751", "dupsko", "SHA-1", BigRange(stringToNumber("dupa", a_z), aaaaaaaRange)) //,
        )

      forAll(hashes) { (hash, dehashed, algo, range) =>
        Given(s"alphabet: $a_z  \n hash: $hash \n algo: $algo \n range: $range ")

        When("Check message is send")
        local ! CheckHalf(range, WorkDetails(hash, algo))

        Then(s"FoundIt message should sent \n")
        expectMsg(300.seconds, FoundIt(dehashed))
      }
    }

  }
}


