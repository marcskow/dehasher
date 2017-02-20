package pl.agh.edu.dehaser.modules.update

import pl.agh.edu.dehaser._

/**
  * Created by razakroner on 2017-02-16.
  */
class UpdateService(repository: UpdateRepository) extends Dehash {
  implicit val ctx = RestSettings.ctx
  val wholeRange = BigRange(1, CoordinatorFSM.nrOfIterations(maxNrOfChars)).toString

  def update(id: Int) = {
    val response = repository.update(id)
    response.map{
      case cracked: Cracked => Response(1, cracked.dehashed)
      case range: Ranges =>
        val c = range.ranges.map(x => Range(x.start.toString, x.end.toString))
        UpdateResult(c, wholeRange)
      case NotFoundIt =>  Response(2, "No solution to given password")
      case NonTaken => Response(3, "Task not taken")
      case NonExisting => Response(4, "Task not existing")
    }
  }

  def cancel(id: Int) = {
    val response = repository.removeTask(id)
    response.map{
      case NotFoundIt => Response(1, s"Task $id canceled")
      case NonTaken => Response(2, "Task not taken")
      case NonExisting => Response(3, "Task not existing")
    }
  }
}