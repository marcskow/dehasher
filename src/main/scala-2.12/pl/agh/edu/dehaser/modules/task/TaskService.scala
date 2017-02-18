package pl.agh.edu.dehaser.modules.task

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskService(repository: TaskRepository) {

  def tasks() = {
    repository.getAllTasks()
  }

  def task(id: String) = {
    repository.getTask(id)
  }

  def createTask(newTask: Task): IdResponse ={
    val taskId = repository.create(newTask)
    IdResponse(taskId)
  }
}
