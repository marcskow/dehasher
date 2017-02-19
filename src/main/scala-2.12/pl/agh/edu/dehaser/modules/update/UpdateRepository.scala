package pl.agh.edu.dehaser.modules.update

import akka.util.Timeout
import scala.concurrent.duration._
import pl.agh.edu.dehaser._
import akka.pattern._

/**
  * Created by razakroner on 2017-02-16.
  */
class UpdateRepository {
  implicit val timeout = Timeout(5 seconds)

  def update(id: Int) = {
    (QueueSettings.queue ? Update(id)).mapTo[Result]
  }

  def removeTask(id: Int) = {
    (QueueSettings.queue ? CancelTask(id)).mapTo[Result]
  }
}
