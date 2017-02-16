package pl.agh.edu.dehaser.modules

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskRepository {
  def getAllTasks() = {
    List(Task("1219", "e1938h129che"),Task("1219", "e1938h129che"))
  }


  def getTask(id:String) = {
    Task("1219", "e1938h129che")
  }
}
