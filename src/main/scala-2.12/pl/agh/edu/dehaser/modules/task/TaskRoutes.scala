package pl.agh.edu.dehaser.modules.task

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import pl.agh.edu.dehaser.RestController
import ch.megard.akka.http.cors.CorsDirectives._
import scala.util.{Success, Failure}

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskRoutes(taskService: TaskService) extends RestController{
  val uri = "task"
  val regex = """[0-9]+""".r

  override def endpoints: Route = cors() {
    path(uri) {
      get{
        onComplete(taskService.tasks()){
          case Success(list) => complete(OK -> list)
          case Failure(ex) => complete(BadRequest -> ex)
        }
      }
    } ~ path(uri/regex){ id =>
      get{
        complete(OK -> taskService.task(id))
      }
    } ~ path(uri){
      post{
        entity(as[Task]){ task =>
          val s = taskService.createTask(task)
          onComplete(s){
            case Success(id) => complete(OK -> id)
            case Failure(ex) => complete(BadRequest -> ex)
          }
        }
      }
    }
  }
}

object TaskRoutes{
  def apply(): TaskRoutes = new TaskRoutes(new TaskService(new TaskRepository()))
}