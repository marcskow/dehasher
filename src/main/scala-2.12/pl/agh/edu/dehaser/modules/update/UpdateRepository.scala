package pl.agh.edu.dehaser.modules.update

import akka.pattern._
import akka.util.Timeout
import pl.agh.edu.dehaser._
import pl.agh.edu.dehaser.messages.{CancelTask, Result, Update}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by razakroner on 2017-02-16.
  */
class UpdateRepository {
  implicit val timeout = Timeout(5 seconds)

  def update(id: Int): Future[Result] = {
    (QueueSettings.queue ? Update(id)).mapTo[Result]
  }

  def removeTask(id: Int): Future[Result] = {
    (QueueSettings.queue ? CancelTask(id)).mapTo[Result]
  }
}
