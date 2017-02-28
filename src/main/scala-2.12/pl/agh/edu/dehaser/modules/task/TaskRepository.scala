package pl.agh.edu.dehaser.modules.task

import akka.pattern._
import akka.util.Timeout
import pl.agh.edu.dehaser._
import pl.agh.edu.dehaser.messages.{DehashIt, ListTasks, ProcessingTask}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps


class TaskRepository {
  implicit val timeout = Timeout(5 seconds)

  def getAllTasks: Future[List[ProcessingTask]] = {
    val response = QueueSettings.queue ? ListTasks
    response.mapTo[List[ProcessingTask]]
  }

  def getTask(id: String): TaskWithId = {
    TaskWithId(12 ,"1219", "e1938h129che", 10)
  }

  def create(newTask : Task): Future[IdResponse]={
    val id = Math.abs((newTask.hash + newTask.algoType).hashCode)
    val response = QueueSettings.queue ? DehashIt(newTask.hash, newTask.algoType, id,  QueueSettings.queue, newTask.range)
    response.mapTo[IdResponse]
  }
}
