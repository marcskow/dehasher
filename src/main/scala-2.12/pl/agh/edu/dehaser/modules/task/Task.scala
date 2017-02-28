package pl.agh.edu.dehaser.modules.task


case class Task (hash: String, algoType: String, range: Int)
case class TaskWithId (id: Int, hash: String, algoType: String, range: Int)
case class IdResponse (id: Int)
