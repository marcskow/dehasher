package pl.agh.edu.dehaser.modules.update

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import pl.agh.edu.dehaser.RestController
import pl.agh.edu.dehaser.modules.task.IdResponse
import ch.megard.akka.http.cors.CorsDirectives._
import scala.util.{Failure, Success}

/**
  * Created by razakroner on 2017-02-16.
  */
class UpdateRoutes(updateService: UpdateService) extends RestController{
  val updateUri = "update"
  val cancelUri = "cancel"
  val id= """[0-9]+""".r

  override def gatherEndpoints : Route = cors() {
    path(updateUri/id) { id =>
      get{
//        MOCK
//        complete(OK -> UpdateResult(5, List(Range("1", "2050001"), Range("2100001", "3150001"), Range("2100001", "21500010")),"321272406"))
        onComplete(updateService.update(id.toInt)){
          case Success(result) => complete(OK -> result)
          case Failure(ex) => complete(BadRequest -> ex)
        }
      }
    } ~ path(cancelUri){
      post{
        entity(as[IdResponse]){ id =>
          onComplete(updateService.cancel(id.id)){
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