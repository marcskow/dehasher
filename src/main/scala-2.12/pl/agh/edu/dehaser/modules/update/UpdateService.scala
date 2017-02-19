package pl.agh.edu.dehaser.modules.update

/**
  * Created by razakroner on 2017-02-16.
  */
class UpdateService(repository: UpdateRepository) {

  def update(id: Int) = {
    repository.update(id)
  }

  def cancel(id: Int) = {
    repository.removeTask(id)
  }
}
