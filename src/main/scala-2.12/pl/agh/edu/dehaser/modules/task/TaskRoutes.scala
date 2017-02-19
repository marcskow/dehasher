package pl.agh.edu.dehaser.modules.task

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import pl.agh.edu.dehaser.RestController

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskRoutes(taskService: TaskService) extends RestController{
  val uri = "task"
  val regex = """[0-9]+""".r

  override def endpoints: Route = {
    path(uri) {
      get{
        complete(OK -> taskService.tasks())
      }
    } ~ path(uri/regex){ id =>
      get{
        complete(OK -> taskService.task(id))
      }
    } ~ path(uri){
      post{
        entity(as[Task]){ task =>
          complete(OK -> taskService.createTask(task))
        }
      }
    }
  }
}

object TaskRoutes{
  def apply(): TaskRoutes = new TaskRoutes(new TaskService(new TaskRepository()))
}