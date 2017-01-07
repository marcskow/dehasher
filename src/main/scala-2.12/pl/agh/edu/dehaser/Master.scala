package pl.agh.edu.dehaser

import akka.actor.{Actor, Props}
import akka.routing.BalancingPool

class Master(hash: String, algo: String) extends Actor {

  import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}

  val defaultAlphabet = """ !\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"""
  val atomSize = 1000
  val maxNrOfchars = 5
  val nrOfWords: Long = nrOfIterations(maxNrOfchars)
  val defaultNumberOfCharacters = 4
  val alphabetSize: Int = defaultAlphabet.length
  private val workerRouter = context.actorOf(
    ClusterRouterPool(BalancingPool(0), ClusterRouterPoolSettings(
      totalInstances = 100, maxInstancesPerNode = 3,
      allowLocalRoutees = true, useRole = None)).props(Props(classOf[Worker], defaultAlphabet)),
    name = "workerRouter3")

  (1 to nrOfWords).grouped(atomSize).map(x => x.head to x.last).foreach(range => workerRouter ! Check(range, hash, algo))

  override def receive: Receive = {
    case FoundIt =>
  }

  def nrOfIterations(maxStringSize: Int): Long = {
    (1 to maxStringSize).map(x => math.pow(alphabetSize, x).toLong).sum
  }

}
