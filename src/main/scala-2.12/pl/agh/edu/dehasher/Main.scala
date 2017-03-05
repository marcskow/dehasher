package pl.agh.edu.dehasher


import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import pl.agh.edu.dehasher.backend.{DehashService, DehashWorker}
import pl.agh.edu.dehasher.messages.DehashIt

object Main {

  def main(args: Array[String]): Unit = {

    val config = if (args.contains("seed"))
      ConfigFactory.parseString(s"akka.remote.netty.tcp.port=" + 2551)
        .withFallback(ConfigFactory.load("coord"))
    else ConfigFactory.load("coord")

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[DehashWorker], name = "statsWorker")
    val service = system.actorOf(Props[DehashService], name = "statsService")

    val a_z = "abcdefghijklmnopqrstuvwxyz"
    val reporter = system.actorOf(Props[Reporter], "reporter")
    if (args.contains("client")) {

      System.out.println("Please write your hash: \n")
      val hash = scala.io.StdIn.readLine()
      System.out.println("Please write algorithm [SHA-256 | MD5 |SHA-1]: \n")
      val algo = scala.io.StdIn.readLine()

      System.out.println("How many characters should have  longest checked words (time grows exponentially) : \n")
      val nrOfChars = scala.io.StdIn.readLine().toInt

      service ! DehashIt(hash, algo, reporter, nrOfChars, a_z)
      System.out.println("Task dispatched \n")
    }
  }
}

class Reporter extends Actor {
  override def receive: Receive = {
    case msg => println(msg)
  }
}