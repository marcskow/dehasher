package pl.agh.edu.dehaser.modules.task

import pl.agh.edu.dehaser.QueueSettings
import pl.agh.edu.dehaser.messages.DehashIt

import scala.concurrent.{ExecutionContextExecutor, Future}


class TaskService(repository: TaskRepository) {
  implicit val ctx: ExecutionContextExecutor = QueueSettings.ctx

  def tasks(): Future[List[Any]] = {
    repository.getAllTasks.map(_.filter{case a: DehashIt => true}.map{
      case DehashIt(hash, algo, id, sender, range) => TaskWithId(id, hash, algo, range)
    }.distinct)
  }

  def task(id: String): TaskWithId = {
    repository.getTask(id)
  }

  def createTask(newTask: Task): Future[IdResponse] ={
    repository.create(newTask)
  }
}
