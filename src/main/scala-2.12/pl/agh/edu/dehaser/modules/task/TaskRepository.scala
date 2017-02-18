package pl.agh.edu.dehaser.modules.task

import akka.util.Timeout
import pl.agh.edu.dehaser._
import akka.pattern._
import scala.concurrent.duration._
import scala.concurrent.Future

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskRepository {
  implicit val timeout = Timeout(5 seconds)

  def getAllTasks = {
    val response = QueueSettings.queue ? ListTasks
    response.mapTo[List[ProcessingTask]]
  }

  def getTask(id:String) = {
    Task("1219", "e1938h129che")
  }

  def create(newTask : Task): Future[IdResponse]={
    val response = QueueSettings.queue ? DehashIt(newTask.hash, newTask.algoType, QueueSettings.queue)
    response.mapTo[IdResponse]
  }
}
