package pl.agh.edu.dehaser.modules.update

/**
  * Created by razakroner on 2017-02-16.
  */
case class Response(code: Int, solution: String)
case class UpdateResult(partialRanges: List[Range], wholeRange: String)
case class Range(start: String, end: String)