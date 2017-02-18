package pl.agh.edu.dehaser.modules.update

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import pl.agh.edu.dehaser.RestController
import pl.agh.edu.dehaser.modules.task.IdResponse

/**
  * Created by razakroner on 2017-02-16.
  */
class UpdateRoutes(updateService: UpdateService) extends RestController{
  val updateUri = "update"
  val cancelUri = "cancel"
  val id= """[0-9]+""".r

  override def endpoints : Route = {
    path(updateUri/id) { id =>
      get{
        complete(OK -> updateService.update(id.toInt))
      }
    } ~ path(cancelUri) {
      post{
        entity(as[IdResponse]){ id =>
          updateService.cancel(id.id)
          complete("Task canceled successfully")
        }
      }
    }
  }
}

object UpdateRoutes {
  def apply(): UpdateRoutes = new UpdateRoutes(new UpdateService(new UpdateRepository()))
}