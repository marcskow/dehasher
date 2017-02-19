package pl.agh.edu.dehaser.modules.update

/**
  * Created by razakroner on 2017-02-16.
  */
class UpdateRepository {
  def update(id: Int) = {
    Result(List(Range("123124","432423"),Range("3213554","65465676")),"346546545434")
  }

  def solution(id: Int) = {
    Response(1,"test")
  }

  def removeTask(id: Int) = {
    println(s"task $id removed")
  }
}
