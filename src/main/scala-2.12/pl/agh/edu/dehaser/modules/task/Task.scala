package pl.agh.edu.dehaser.modules.task

/**
  * Created by razakroner on 2017-02-16.
  */
case class Task (hash: String, algoType: String, range: Int)
case class TaskWithId (id: Int, hash: String, algoType: String, range: Int)
case class IdResponse (id: Int)
