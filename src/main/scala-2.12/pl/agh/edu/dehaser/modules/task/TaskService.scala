package pl.agh.edu.dehaser.modules.task

import pl.agh.edu.dehaser.{DehashIt, RestSettings}

import scala.concurrent.Future

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskService(repository: TaskRepository) {
  implicit val ctx = RestSettings.ctx
  def tasks() = {
    repository.getAllTasks.map(_.map{
      case DehashIt(hash, algo, id, sender, range) => Task(hash, algo, range)
      case _ =>
    })
  }

  def task(id: String) = {
    repository.getTask(id)
  }

  def createTask(newTask: Task): Future[IdResponse] ={
    repository.create(newTask)
  }
}
