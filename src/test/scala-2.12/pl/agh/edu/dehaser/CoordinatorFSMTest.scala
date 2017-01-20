package pl.agh.edu.dehaser

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.concurrent.duration._

//noinspection TypeAnnotation
class CoordinatorFSMTest extends TestKit(ActorSystem("NodeActorSpec")) with ImplicitSender
  with FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks with Dehash with BeforeAndAfterAll {

  val a_z = "abcdefghijklmnopqrstuvwxyz"
  val testRange = stringToNumber("test", a_z)
  val tezzRange = stringToNumber("tezz", a_z)
  val dupaRange = stringToNumber("dupa", a_z)
  val aaaaaRange = stringToNumber("aaaaa", a_z)
  val aaaaaaaRange = stringToNumber("aaaaaaa", a_z)
  val aaRange = stringToNumber("aa", a_z)
  val xyRange = stringToNumber("xy", a_z)

  val masterProbe = TestProbe("master")
  val aggregatorProbe = TestProbe("aggregator")
  val quueStub = TestProbe("queue")

  val xyaaRange = stringToNumber("xyaa", a_z)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  describe("Coordinator") {
    // TODO: test splitting task

    it("should send DidMyWork message") {
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, nrOfWorkers = 2, quueStub.ref.path), "coordinator1")
      val hashes =
        Table(
          ("hash", "dehashed", "algo", "range"), // First tuple defines column names
          ("2254139645ffdd350372a68e0cc4271731019751", "dupsko", "SHA-1", BigRange(aaRange, dupaRange)) //,
        )
      forAll(hashes) { (hash, dehashed, algo, range) =>
        Given(s"alphabet: $a_z  \n hash: $hash \n algo: $algo \n range: $range ")
        When("Check message is send")
        coordinator ! CheckHalf(range, WorkDetails(hash, algo), masterProbe.ref, aggregatorProbe.ref)

        Then(s"DidMyWork message should sent \n")
        expectMsgType[DidMyWork]
      }
    }

    it("should find solution") {
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = quueStub.ref.path), "coordinator4")
      val hashes =
        Table(
          ("hash", "dehashed", "algo", "range"), // First tuple defines column names
          ("f62d6f44dd33c275a0656e11ef9fd793bebd92ce68d2f23e69bb279ef74e3ac6", "teww", "SHA-256", BigRange(testRange, tezzRange)),
          ("d16fb36f0911f878998c136191af705e", "xyz", "MD5", BigRange(xyRange, xyaaRange)) //,
          //          ("2254139645ffdd350372a68e0cc4271731019751", "dupsko", "SHA-1", BigRange(stringToNumber("daaaaa", a_z), aaaaaaaRange)) //, //long running
          //            ("2254139645ffdd350372a68e0cc4271731019751", "dupsko", "SHA-1", BigRange(stringToNumber("dupsk", a_z), aaaaaaaRange)) //, //long running
        )

      forAll(hashes) { (hash, dehashed, algo, range) =>
        Given(s"alphabet: $a_z  \n hash: $hash \n algo: $algo \n range: $range ")

        When("Check message is send")
        coordinator ! CheckHalf(range, WorkDetails(hash, algo), masterProbe.ref, aggregatorProbe.ref)

        Then(s"FoundIt message should sent \n")
        masterProbe.expectMsg(300.seconds, FoundIt(dehashed))
      }
    }

    it("should offerTask to queue after obtaining large range[CheckHalf] ") {
      Given("range above split threshold")
      val queue = TestProbe("queue")
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = queue.ref.path), "coordinator5")
      val range = BigRange(dupaRange, aaaaaaaRange)

      When("CheckHalf message is sent")
      coordinator ! CheckHalf(range, WorkDetails("kjnkbbuyvb", "SHA-1"), masterProbe.ref, aggregatorProbe.ref)

      Then("queue should get OfferTask msg")
      queue.ignoreMsg { case GiveMeWork => true } // after initial state
      queue.expectMsg(OfferTask)
    }

    it("should offerTask to queue after obtaining large range[DehashIt] ") {
      Given("range above split threshold")
      val queue = TestProbe("queue")
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = queue.ref.path), "coordinator")
      val range = BigRange(dupaRange, aaaaaaaRange)

      When("DehashIt message is sent")
      queue.ignoreMsg { case GiveMeWork => true } // after initial state
      coordinator ! DehashIt("kjnkbbuyvb", "SHA-1", TestProbe().ref)

      Then("queue should get OfferTask msg")
      queue.expectMsg(OfferTask)
    }

    it("should send GiveMeWork after creation") {
      val queue = TestProbe("queue")
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = queue.ref.path), "coordinator8")

      queue.expectMsg(GiveMeWork)
    }

  }
}
