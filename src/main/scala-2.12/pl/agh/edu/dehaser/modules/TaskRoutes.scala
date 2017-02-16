package pl.agh.edu.dehaser.modules

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.unmarshalling.{Unmarshaller, Unmarshal}
import pl.agh.edu.dehaser.RestController
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskRoutes(taskService: TaskService) extends RestController{
  val uri = "task"
  val regex = """[a-zA-Z]+""".r

  override def endpoints: Route = {
    path(uri) {
      get{
        complete(taskService.tasks())
      }
    } ~ path(uri/regex){ id =>
      get{
        complete(OK -> taskService.task(id))
      }
    }
  }
}

object TaskRoutes{
  def apply(): TaskRoutes = new TaskRoutes(new TaskService(new TaskRepository()))
}