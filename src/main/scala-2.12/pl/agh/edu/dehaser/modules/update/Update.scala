package pl.agh.edu.dehaser.modules.update

/**
  * Created by razakroner on 2017-02-16.
  */
case class Response(code: Int, soultion: String)
case class Result(partialRanges: List[Range], wholeRange: String)
case class Range(start: String, end: String)