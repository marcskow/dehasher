package pl.agh.edu.dehaser.messages

import akka.actor.ActorRef

case class DehashIt(hash: String, algo: String, taskId: Int, originalSender: ActorRef, maxIter: Int) extends ProcessingTask

case object GiveMeWork

case object OfferTask

case object ListTasks

sealed trait ProcessingTask

case class AskHim(otherCoordinator: ActorRef) extends ProcessingTask
