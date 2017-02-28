package pl.agh.edu.dehaser


import akka.actor.{ActorPath, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult
import com.typesafe.config.ConfigFactory
import pl.agh.edu.dehaser.backend.CoordinatorFSM
import pl.agh.edu.dehaser.rest.RestRoutes
import pl.agh.edu.dehaser.rest.modules.task.TaskRoutes
import pl.agh.edu.dehaser.rest.modules.update.UpdateRoutes

object Main extends RestRoutes {
  lazy val allRoutes = List(TaskRoutes(), UpdateRoutes())

  def main(args: Array[String]): Unit = {
    args.headOption match {
      case Some("queue") => startQueueSystem()
      case Some("client") => DummyClient.startClientSystem()
      case None => startCoordinatorSystem()
    }
  }

  def startQueueSystem(): Unit = {

    import pl.agh.edu.dehaser.rest.QueueSettings._
    val routeFlow = RouteResult.route2HandlerFlow(controllers(allRoutes))
    val bind = Http().bindAndHandle(routeFlow, Settings.hostname, Settings.restPort)

    import scala.util.{Failure, Success}
    bind.onComplete {
      case Success(success) => println(s"Successfully binded to addres ${success.localAddress}")
      case Failure(ex) => println("Failed to bind to address")
    }
    println("Started queueSystem - waiting for messages")
  }

  def startCoordinatorSystem(): Unit = {
    lazy val remotePath: ActorPath = ActorPath.fromString(Settings.queuePath)
    val a_z = "abcdefghijklmnopqrstuvwxyz"
    val system =
      ActorSystem("coordinatorSystem", ConfigFactory.load("coord"))
    system.actorOf(CoordinatorFSM.props(alphabet = a_z, queuePath = remotePath), "coordinator")

  }


}
