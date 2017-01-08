package pl.agh.edu.dehaser

import java.util.concurrent.ThreadLocalRandom

import akka.actor.{Actor, ActorSystem, Address, Cancellable, Props, RelativeActorPath, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}
import com.typesafe.config.ConfigFactory

import scala.collection.immutable.Seq
import scala.concurrent.duration._

object MainSample {
  def main(args: Array[String]): Unit = {
    startup(args)
  }

  def startup(ports: Array[String]): Unit = {
    ports foreach { port =>
      // Override the configuration of the port when specified as program argument
      val config =
        ConfigFactory.parseString(s"akka.remote.netty.tcp.port=" + port).withFallback(
          ConfigFactory.parseString("akka.cluster.roles = [compute]")).
          withFallback(ConfigFactory.load("application"))

      val system = ActorSystem("ClusterSystem", config)

      system.actorOf(Props[DehashWorker], name = "dehashWorker")
      system.actorOf(DehashService.props, name = "dehashService")
      //      system.actorOf(Props(), name = "dehashService")
    }
  }
}

object DehasherSampleClient {

  def main(args: Array[String]): Unit = {
    // note that client is not a compute node, role not defined
    val system = ActorSystem("ClusterSystem")
    system.actorOf(Props(classOf[DehasherSampleClient], "/user/dehashService"), "client")
  }
}

class DehasherSampleClient(servicePath: String) extends Actor {
  val cluster = Cluster(context.system)
  val servicePathElements: Seq[String] = servicePath match {
    case RelativeActorPath(elements) => elements
    case _ => throw new IllegalArgumentException(
      "servicePath [%s] is not a valid relative actor path" format servicePath)
  }

  import context.dispatcher

  val tickTask: Cancellable = context.system.scheduler.scheduleOnce(2.seconds, self, "tick")

  var nodes = Set.empty[Address]

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent], classOf[ReachabilityEvent])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    tickTask.cancel()
  }

  //noinspection TypeAnnotation
  def receive = {
    case "tick" if nodes.nonEmpty =>
      // just pick any one
      val address = nodes.toIndexedSeq(ThreadLocalRandom.current.nextInt(nodes.size))
      val service = context.actorSelection(RootActorPath(address) / servicePathElements)
      val hash =
        service ! DehashIt("90b94d224ee82c837143ea6f0308c596f0142612678a036c65041b246d52df22", "SHA-256") // dupsko


    case (crackedPass, whoCrackedIt) //: Tuple2[String, ActorRef]
    =>
      println(s"pass: $crackedPass , cracker: $whoCrackedIt")

    case state: CurrentClusterState =>
      nodes = state.members.collect {
        case m if m.hasRole("compute") && m.status == MemberStatus.Up => m.address
      }

    case MemberUp(m)
      if m.hasRole("compute")
    => nodes += m.address

    case other: MemberEvent => nodes -= other.member.address

    case UnreachableMember(m)
    => nodes -= m.address

    case ReachableMember(m)
      if m.hasRole("compute")
    => nodes += m.address
  }

}
