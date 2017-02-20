package pl.agh.edu.dehaser

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import pl.agh.edu.dehaser.modules.task.TaskRoutes
import pl.agh.edu.dehaser.modules.update.UpdateRoutes

/**
  * Created by razakroner on 2017-02-16.
  */
trait RestRoutes {
  def controllers(list: List[RestController]) : Route = {
    list.map(_.gatherEndpoints)
      .reduce(_~_)
  }
}
