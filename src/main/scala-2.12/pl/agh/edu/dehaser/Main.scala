package pl.agh.edu.dehaser


import akka.actor.{ActorPath, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult
import com.typesafe.config.ConfigFactory
import pl.agh.edu.dehaser.modules.task.TaskRoutes
import pl.agh.edu.dehaser.modules.update.UpdateRoutes

object Main extends RestRoutes {
  val allRoutes = List(TaskRoutes(), UpdateRoutes())


  def main(args: Array[String]): Unit = {
        startQueueSystem
//    args.headOption match {
//      case Some("Queue") => startQueueSystem()
//      case None => startCoordinatorSystem()
//    }
  }

  def startQueueSystem(): Unit = {

    import QueueSettings._
    val routeFlow = RouteResult.route2HandlerFlow(controllers(allRoutes))
    val bind = Http().bindAndHandle(routeFlow, QueueSettings.HOST, QueueSettings.PORT)

    import scala.util.{Success,Failure}
    bind.onComplete {
      case Success(success) => println(s"Successfully binded to addres ${success.localAddress}")
      case Failure(ex) => println("Failed to bind to address")
    }
    println("Started queueSystem - waiting for messages")
  }

  def startCoordinatorSystem(): Unit = {
    lazy val remotePath: ActorPath = ActorPath.fromString("akka.tcp://Rest@127.0.0.1:2552/user/queue")
    val a_z = "abcdefghijklmnopqrstuvwxyz"

    val system =
      ActorSystem("coordinatorSystem", ConfigFactory.load("coord"))
    system.actorOf(CoordinatorFSM.props(alphabet = a_z, queuePath = remotePath), "coordinator")

  }
}
