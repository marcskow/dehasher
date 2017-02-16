package pl.agh.edu.dehaser

import pl.agh.edu.dehaser.modules.TaskRoutes
import akka.http.scaladsl.server.RouteConcatenation._
/**
  * Created by razakroner on 2017-02-16.
  */
trait RestRoutes {
  def controllers = {
    List(TaskRoutes()).map(_.endpoints).reduce(_~_)
  }
}
