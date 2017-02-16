package pl.agh.edu.dehaser.modules

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
}
