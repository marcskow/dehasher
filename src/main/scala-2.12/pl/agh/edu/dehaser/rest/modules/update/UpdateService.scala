package pl.agh.edu.dehaser.rest.modules.update

import pl.agh.edu.dehaser.backend.Dehash
import pl.agh.edu.dehaser.backend.range.BigRange
import pl.agh.edu.dehaser.messages._
import pl.agh.edu.dehaser.rest.QueueSettings

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.implicitConversions


class UpdateService(repository: UpdateRepository) extends Dehash {
  implicit val ctx: ExecutionContextExecutor = QueueSettings.ctx

  def update(id: Int): Future[Product with Serializable] = {
    val response = repository.update(id)
    response.map{
      case cracked: Cracked => Response(1, cracked.dehashed)
      case NotFoundIt =>  Response(2, "No solution to given password")
      case NonTaken => Response(3, "Task not taken")
      case NonExisting => Response(4, "Task not existing")
      case range: Ranges =>
        val response: List[Range] = range.ranges.map(convertRespoonse)
        UpdateResult(5, response, range.wholeRange)
    }
  }

  def cancel(id: Int): Future[Response] = {
    val response = repository.removeTask(id)
    response.map{
      case NotFoundIt => Response(1, s"Task $id canceled")
      case NonTaken => Response(2, "Task not taken")
      case NonExisting => Response(3, "Task not existing")
      case _ => Response(8, "???")
    }
  }
  implicit def convertRespoonse(range: BigRange): Range = Range(range.start.toString(), range.end.toString())

}