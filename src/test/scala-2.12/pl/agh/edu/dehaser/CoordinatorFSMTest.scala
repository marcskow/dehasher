package pl.agh.edu.dehaser

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks
import pl.agh.edu.dehaser.backend.range.BigRange
import pl.agh.edu.dehaser.backend.{CoordinatorFSM, Dehash}
import pl.agh.edu.dehaser.messages._

import scala.concurrent.duration._
import scala.language.postfixOps

//noinspection TypeAnnotation
class CoordinatorFSMTest extends TestKit(ActorSystem("NodeActorSpec")) with ImplicitSender
  with FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks with Dehash with BeforeAndAfterAll {

  val a_z = "abcdefghijklmnopqrstuvwxyz"
  val testRange = stringToNumber("test", a_z)
  val tezzRange = stringToNumber("tezz", a_z)
  val aaaaaaaRange = stringToNumber("aaaaaaa", a_z)
  val aaRange = stringToNumber("aa", a_z)
  val lalaRange = stringToNumber("lala", a_z)
  val xyaaRange = stringToNumber("xyaa", a_z)
  val xyRange = stringToNumber("xy", a_z)

  val masterProbe = TestProbe("master")
  val aggregatorProbe = TestProbe("aggregator")
  val queueStub = TestProbe("queue")


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("Coordinator") {


    it("should find solution") {
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = queueStub.ref.path), "coordinator4")
      val hashes =
        Table(
          ("hash", "dehashed", "algo", "range"), // First tuple defines column names
          ("f62d6f44dd33c275a0656e11ef9fd793bebd92ce68d2f23e69bb279ef74e3ac6", "teww", "SHA-256", BigRange(testRange, tezzRange)),
          ("d16fb36f0911f878998c136191af705e", "xyz", "MD5", BigRange(xyRange, xyaaRange))
        )

      forAll(hashes) { (hash, dehashed, algo, range) =>
        Given(s"alphabet: $a_z  \n hash: $hash \n algo: $algo \n range: $range ")

        When("Check message is send")
        coordinator ! CheckHalf(List(range), WorkDetails(hash, algo), masterProbe.ref, aggregatorProbe.ref)

        Then(s"FoundIt message should sent \n")
        masterProbe.expectMsg(35.seconds, FoundIt(dehashed))
      }
    }

    it("should offerTask to queue after obtaining large range[CheckHalf] ") {
      Given("range above split threshold")
      val queue = TestProbe("queue")
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = queue.ref.path), "coordinator5")
      val range = BigRange(lalaRange, aaaaaaaRange)

      When("CheckHalf message is sent")
      coordinator ! CheckHalf(List(range), WorkDetails("kjnkbbuyvb", "SHA-1"), masterProbe.ref, aggregatorProbe.ref)

      Then("queue should get OfferTask msg")
      queue.fishForMessage(1 second) { case OfferTask => true; case _ => false }
    }

    it("should offerTask to queue after obtaining large range[DehashIt] ") {
      Given("range above split threshold")
      val queue = TestProbe("queue")
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = queue.ref.path), "coordinator")
      val range = BigRange(lalaRange, aaaaaaaRange)

      When("DehashIt message is sent")
      coordinator ! DehashIt("kjnkbbuyvb", "SHA-1", taskId = 123, TestProbe().ref, maxIter = 8)

      Then("queue should get OfferTask msg")
      queue.fishForMessage(1 second) { case OfferTask => true; case _ => false }
    }

    it("should send GiveMeWork after creation") {
      val queue = TestProbe("queue")
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = queue.ref.path), "coordinator8")

      queue.expectMsg(GiveMeWork)
    }

  }
}
