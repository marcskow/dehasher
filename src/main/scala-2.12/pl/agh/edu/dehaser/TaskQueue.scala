package pl.agh.edu.dehaser

import akka.actor.{FSM, Props}


class TaskQueue extends FSM[QueueState, QueueData] {

  startWith(QueueStateImpl, QueueData(List()))

  when(QueueStateImpl) {
    case Event(OfferTask, QueueData(list)) => stay() using QueueData(list :+ AskHim(sender()))
    case Event(task: DehashIt, QueueData(list)) => stay() using QueueData(list :+ task)
    case Event(GiveMeWork, QueueData(list)) =>
      list.headOption.foreach(task => sender() ! task)
      val tail = if (list.nonEmpty) list.tail else List()
      stay() using QueueData(tail)
  }

  initialize()
}

object TaskQueue {
  def props: Props = Props(new TaskQueue())
}


sealed trait QueueState

case object QueueStateImpl extends QueueState

case class QueueData(tasks: List[ProcessingTask])