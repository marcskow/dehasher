package pl.agh.edu.dehaser

import akka.actor.{ActorRef, LoggingFSM, Props}
import pl.agh.edu.dehaser.messages._
import pl.agh.edu.dehaser.modules.task.IdResponse


class TaskQueue extends LoggingFSM[QueueState, QueueData] {

  startWith(QueueStateImpl, QueueData(List(),Map(), Map()))

  when(QueueStateImpl) {
    case Event(OfferTask, QueueData(list, workers, taskMapper)) => stay() using QueueData(list :+ AskHim(sender()), workers, taskMapper)
    case Event(task: DehashIt, QueueData(list, workers, taskMapper)) =>
      val id = Math.abs((task.hash + task.algo).hashCode)
      sender() ! IdResponse(id)
      stay() using QueueData(list :+ task, workers + (id -> None), taskMapper + (id -> task.hash))

    case Event(GiveMeWork, QueueData(list, workers, taskMapper)) =>
      if (list.nonEmpty) {
        list.head match {
          case initialTask: DehashIt => sender() ! initialTask
            val id = Math.abs((initialTask.hash + initialTask.algo).hashCode)
            val tail = if (list.nonEmpty) list.tail else List()
            stay() using QueueData(tail, workers + (id -> Some(sender())), taskMapper)
          case task => sender() ! task
            val tail = if (list.nonEmpty) list.tail else List()
            stay() using QueueData(tail, workers, taskMapper)
        }
      }else{
        stay() using QueueData(list, workers, taskMapper)
      }


    case Event(ListTasks, QueueData(list, workers, taskMapper)) =>
      sender() ! list
      stay() using QueueData(list, workers, taskMapper)
    case Event(update: Update, QueueData(list, workers, taskMapper)) =>
      workers.getOrElse(update.taskId, "NoneTaken") match {
        case Some(worker:ActorRef) => worker forward update
        case None => sender() ! NonTaken
        case "NoneTaken" => sender() ! NonExisting
      }
      stay() using QueueData(list, workers, taskMapper)
    case Event(x: CancelTask, QueueData(list, workers, taskMapper)) =>
      val updatedWorkers = workers - x.id
      val o = taskMapper(x.id)
      workers.getOrElse(x.id, "NoneTaken") match {
        case Some(worker:ActorRef) => worker forward CancelComputation
        case None => sender() ! NonTaken
        case "NoneTaken" => sender() ! NonExisting
      }
      stay() using QueueData(list.filter(_!=o), updatedWorkers, taskMapper)
  }
  initialize()
}

object TaskQueue {
  def props: Props = Props(new TaskQueue())
}


sealed trait QueueState

case object QueueStateImpl extends QueueState

case class QueueData(tasks: List[ProcessingTask], initialWorkers: Map[Int, Option[ActorRef]], taksMapper: Map[Int, String])