package pl.agh.edu.dehaser.modules.task

import akka.actor.ActorSystem
import pl.agh.edu.dehaser.{DehashIt, TaskQueue}
import akka.pattern._

import scala.concurrent.Future

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskRepository {
  def getAllTasks() = {
    List(Task("1219", "e1938h129che"), Task("1219", "e1938h129che"))
  }
//    val queue = system.actorOf(TaskQueue.props,"queue")
//    ask(queue, DehashIt())
//    receive{
//      case AllTasks(List[Task]) =>

  def getTask(id:String) = {
    Task("1219", "e1938h129che")
  }

  def create(newTask : Task): Future[IdResponse]={

    val system = ActorSystem("QueueSystem")
    val queue = system.actorOf(TaskQueue.props,"queue")
    val f = queue ? DehashIt(newTask.hash, newTask.algoType, queue)
    f.mapTo[IdResponse]
  }
}
