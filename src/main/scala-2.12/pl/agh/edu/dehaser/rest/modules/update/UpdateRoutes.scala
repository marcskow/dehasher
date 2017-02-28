package pl.agh.edu.dehaser.rest.modules.update

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.CorsDirectives._
import pl.agh.edu.dehaser.rest.RestController
import pl.agh.edu.dehaser.rest.modules.task.IdResponse

import scala.util.matching.Regex
import scala.util.{Failure, Success}


class UpdateRoutes(updateService: UpdateService) extends RestController {
  val updateUri = "update"
  val cancelUri = "cancel"
  val id: Regex = """[0-9]+""".r

  override def gatherEndpoints: Route = cors() {
    path(updateUri / id) { id =>
      get {
        onComplete(updateService.update(id.toInt)) {
          case Success(result) => complete(OK -> result)
          case Failure(ex) => complete(BadRequest -> ex)
        }
      }
    } ~ path(cancelUri) {
      post {
        entity(as[IdResponse]) { id =>
          onComplete(updateService.cancel(id.id)) {
            case Success(result) => complete(OK -> result)
            case Failure(ex) => complete(BadRequest -> ex)
          }
        }
      }
    }
  }
}

object UpdateRoutes {
  def apply(): UpdateRoutes = new UpdateRoutes(new UpdateService(new UpdateRepository()))
}