package pl.agh.edu.dehaser

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import connection.dtos._
trait Routes {
  val routes = pathPrefix("task"){
    post{
      entity(as[Task]) { task =>
        private val queue = context.actorSelection(queuePath)
        complete(OK ->"done")
      }
    }
  }
}
