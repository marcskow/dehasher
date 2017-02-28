package pl.agh.edu.dehaser.modules.update


case class Response(code: Int, solution: String)
case class UpdateResult(code: Int, partialRanges: List[Range], wholeRange: String)
case class Range(start: String, end: String)