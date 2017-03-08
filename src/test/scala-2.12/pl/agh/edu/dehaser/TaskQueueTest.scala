package pl.agh.edu.dehaser

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks
import pl.agh.edu.dehaser.backend.{Dehash, TaskQueue}
import pl.agh.edu.dehaser.messages.{AskHim, DehashIt, GiveMeWork, OfferTask}
import pl.agh.edu.dehaser.rest.modules.task.IdResponse

class TaskQueueTest extends TestKit(ActorSystem("NodeActorSpec")) with ImplicitSender
  with FunSpecLike with GivenWhenThen with Matchers with TableDrivenPropertyChecks with Dehash with BeforeAndAfterAll {


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  describe("Queue ") {

    it("distribute tasks") {
      Given("Empty Queue")
      val queue = system.actorOf(Props[TaskQueue])

      When("someone offers task")
      queue ! OfferTask
      And("someone sends DehashIt")
      queue ! DehashIt("dfdgfhgz", "MD5", taskId = 123, TestProbe().ref, maxIter = 8)

      And("someone else sends GiveMeWork")
      queue ! GiveMeWork
      And("someone else sends GiveMeWork")
      queue ! GiveMeWork
      And("someone else sends GiveMeWork")
      queue ! GiveMeWork


      Then("Queue should answer IdResponse")
      expectMsgType[IdResponse]

      And("Queue should answer AskHim")
      expectMsgType[AskHim]

      And("Queue should answer DehashIt")
      expectMsgType[DehashIt]

      And("Queue should not answer")
      expectNoMsg()


    }
  }
}
