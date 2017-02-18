package pl.agh.edu.dehaser.modules.task

import pl.agh.edu.dehaser.{Main, DehashIt}

import scala.concurrent.Future

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskService(repository: TaskRepository) {
  implicit val ctx = Main.ctx
  def tasks() = {
    repository.getAllTasks.map(_.map{
      case DehashIt(hash, algo, sender) => Task(hash, algo)
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
