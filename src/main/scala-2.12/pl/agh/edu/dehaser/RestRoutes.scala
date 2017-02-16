package pl.agh.edu.dehaser

import pl.agh.edu.dehaser.modules.TaskRoutes

/**
  * Created by razakroner on 2017-02-16.
  */
trait RestRoutes {
  def controllers = handle {
    val default = List(
      TaskRoutes
    )
      .map(_.endpoint)

    default.reduce(_~_)
  }
}
