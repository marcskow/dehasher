package pl.agh.edu.dehaser

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit, TestProbe}
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.concurrent.duration._

//noinspection TypeAnnotation
class CoordinatorFSMTest extends TestKit(ActorSystem("NodeActorSpec")) with ImplicitSender
  with FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks with Dehash with BeforeAndAfterAll {

  val a_z = "abcdefghijklmnopqrstuvwxyz"
  val testRange = stringToNumber("test", a_z)
  val aaRange = stringToNumber("aa", a_z)
  val xyRange = stringToNumber("xy", a_z)

  val masterProbe = TestProbe("master")
  val aggregatorProbe = TestProbe("aggregator")
  val queueStub = TestProbe("queue")

  val xyaaRange = stringToNumber("xyaa", a_z)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  describe("Coordinator") {
    // TODO: test splitting task


    it("should send GiveMeWork after creation") {
      val queue = TestProbe("queue")
      val coordinator = system.actorOf(CoordinatorFSM.props(a_z, queuePath = queue.ref.path), "coordinator8")

      queue.expectMsg(GiveMeWork)
    }

  }
}
