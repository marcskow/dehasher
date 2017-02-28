package pl.agh.edu.dehaser

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import pl.agh.edu.dehaser.messages.DehashIt


object DummyClient {
  def startClientSystem(): Unit = {

    val system = ActorSystem("ClientSystem", ConfigFactory.load("client"))
    val reporter = system.actorOf(Props[Reporter], "reporter")

    while (true) {
      val queue = system.actorSelection(Settings.queuePath)
      System.out.println("Please write your hash: \n")
      val hash = scala.io.StdIn.readLine()
      System.out.println("Please write algorithm [SHA-256 | MD5 |SHA-1]: \n")
      val algo = scala.io.StdIn.readLine()

      System.out.println("How many characters should have  longest checked words (time grows exponentially) : \n")
      val nrOfChars = scala.io.StdIn.readLine().toInt

      val id = (Math.random() * 1000000).toInt
      queue ! DehashIt(hash, algo, id, reporter, nrOfChars)
      System.out.println("Task dispatched \n")
    }


  }
}


class Reporter extends Actor {
  override def receive: Receive = {
    case msg => println(msg)
  }
}
