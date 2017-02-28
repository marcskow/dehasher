package pl.agh.edu.dehaser

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._


trait RestRoutes {
  def controllers(list: List[RestController]) : Route = {
    list.map(_.gatherEndpoints)
      .reduce(_~_)
  }
}
