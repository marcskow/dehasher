package pl.agh.edu.dehaser.modules.task

import pl.agh.edu.dehaser.QueueSettings
import pl.agh.edu.dehaser.messages.DehashIt

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskService(repository: TaskRepository) {
  implicit val ctx: ExecutionContextExecutor = QueueSettings.ctx

  def tasks(): Future[List[Any]] = {
    repository.getAllTasks.map(_.map{
      case DehashIt(hash, algo, id, sender, range) => Task(hash, algo, range)
      case _ =>
    })
  }

  def task(id: String): Task = {
    repository.getTask(id)
  }

  def createTask(newTask: Task): Future[IdResponse] ={
    repository.create(newTask)
  }
}
