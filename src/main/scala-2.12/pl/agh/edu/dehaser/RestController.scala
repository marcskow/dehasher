package pl.agh.edu.dehaser

import akka.http.scaladsl.server.{Directives, Route}
import pl.agh.edu.dehaser.utils.Json4sFormats
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
/**
  * Created by razakroner on 2017-02-16.
  */
trait RestController extends Directives with Json4sFormats with Json4sSupport
{
  val notFound = 404
  def gatherEndpoints: Route = reject
}
