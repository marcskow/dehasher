package pl.agh.edu.dehaser

import akka.actor.{ActorRef, LoggingFSM, Props}
import pl.agh.edu.dehaser.messages._
import pl.agh.edu.dehaser.modules.task.IdResponse


class TaskQueue extends LoggingFSM[QueueState, QueueData] {

  startWith(QueueStateImpl, QueueData(List(),Map()))

  when(QueueStateImpl) {
    case Event(OfferTask, QueueData(list, workers)) => stay() using QueueData(list :+ AskHim(sender()), workers)
    case Event(task: DehashIt, QueueData(list, workers)) =>
      val id = Math.abs((task.hash + task.algo).hashCode)
      sender() ! IdResponse(id)
      stay() using QueueData(list :+ task, workers + (id -> None))

    case Event(GiveMeWork, QueueData(list, workers)) =>
      if (list.nonEmpty) {
        list.head match {
          case initialTask: DehashIt => sender() ! initialTask
            val id = Math.abs((initialTask.hash + initialTask.algo).hashCode)
            val tail = if (list.nonEmpty) list.tail else List()
            stay() using QueueData(tail, workers + (id -> Some(sender())))
          case task => sender() ! task
            val tail = if (list.nonEmpty) list.tail else List()
            stay() using QueueData(tail, workers)
        }
      }else{
        stay() using QueueData(list, workers)
      }


    case Event(ListTasks, QueueData(list, workers)) =>
      sender() ! list
      stay() using QueueData(list, workers)
    case Event(update: Update, QueueData(list, workers)) =>
      workers.getOrElse(update.taskId, "NoneTaken") match {
        case Some(worker:ActorRef) => worker forward update
        case None => sender() ! NonTaken
        case "NoneTaken" => sender() ! NonExisting
      }
      stay() using QueueData(list, workers)

    case Event(x: CancelTask, QueueData(list, workers)) =>
      val updatedWorkers = workers - x.id
      workers.getOrElse(x.id, "NoneTaken") match {
        case Some(worker:ActorRef) => worker forward CancelComputation
        case None => sender() ! NonTaken
        case "NoneTaken" => sender() ! NonExisting
      }
      val upList = list.filter{
        case y: DehashIt => y.taskId!=x.id
      }
      stay() using QueueData(upList, updatedWorkers)
  }
  initialize()
}

object TaskQueue {
  def props: Props = Props(new TaskQueue())
}


sealed trait QueueState

case object QueueStateImpl extends QueueState

case class QueueData(tasks: List[ProcessingTask], initialWorkers: Map[Int, Option[ActorRef]])