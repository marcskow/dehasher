package pl.agh.edu.dehaser.modules.update

import pl.agh.edu.dehaser.{Dehash, CoordinatorFSM, BigRange, Main}

/**
  * Created by razakroner on 2017-02-16.
  */
class UpdateService(repository: UpdateRepository) extends Dehash {
  implicit val ctx = Main.ctx
  val wholeRange = BigRange(1, CoordinatorFSM.nrOfIterations(maxNrOfChars)).toString

  def update(id: Int) = {
    val response = repository.update(id)
    response.map(_.map(x => Range(x.start.toString, x.end.toString))).map(x=> Result(x, wholeRange))
  }

  def cancel(id: Int) = {
    repository.removeTask(id)
  }
}
