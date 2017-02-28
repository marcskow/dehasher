package pl.agh.edu.dehaser


import akka.actor.{ActorPath, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult
import com.typesafe.config.ConfigFactory
import pl.agh.edu.dehaser.modules.task.TaskRoutes
import pl.agh.edu.dehaser.modules.update.UpdateRoutes

object Main extends RestRoutes {
  lazy val allRoutes = List(TaskRoutes(), UpdateRoutes())


  def main(args: Array[String]): Unit = {
    args.headOption match {
      case Some("Queue") => startQueueSystem()
      case None => startCoordinatorSystem()
    }
  }

  def startQueueSystem(): Unit = {

    import QueueSettings._
    val routeFlow = RouteResult.route2HandlerFlow(controllers(allRoutes))
    val bind = Http().bindAndHandle(routeFlow, QueueSettings.HOST, QueueSettings.PORT)

    import scala.util.{Failure, Success}
    bind.onComplete {
      case Success(success) => println(s"Successfully binded to addres ${success.localAddress}")
      case Failure(ex) => println("Failed to bind to address")
    }
    println("Started queueSystem - waiting for messages")
  }

  def startCoordinatorSystem(): Unit = {
    lazy val remotePath: ActorPath = ActorPath.fromString("akka.tcp://Rest@192.168.43.220:2552/user/queue")
    val a_z = "abcdefghijklmnopqrstuvwxyz"
    val config = ConfigFactory.load("coord")
    val system =
      ActorSystem("coordinatorSystem", config)
    system.actorOf(CoordinatorFSM.props(alphabet = a_z, queuePath = remotePath), "coordinator")

  }


}
