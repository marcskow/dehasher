package pl.agh.edu.dehaser.connection.dtos


import akka.http.scaladsl.server.Directives._
/**
  * Created by razakroner on 2017-01-24.
  */
class TaskRoute {
  val  route = pathPrefix("task") {
    pathEnd {
      post{
        entity(as[Task])
      }
    }
  }
}
