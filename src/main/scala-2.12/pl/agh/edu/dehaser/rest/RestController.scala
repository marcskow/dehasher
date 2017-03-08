package pl.agh.edu.dehaser.rest

import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import pl.agh.edu.dehaser.rest.utils.Json4sFormats

trait RestController extends Directives with Json4sFormats with Json4sSupport
{
  val notFound = 404
  def gatherEndpoints: Route = reject
}
