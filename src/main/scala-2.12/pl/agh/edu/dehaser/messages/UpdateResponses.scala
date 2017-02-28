package pl.agh.edu.dehaser.messages

import pl.agh.edu.dehaser.backend.range.BigRange

sealed trait Result

case class Cracked(dehashed: String) extends Result

case class Ranges(ranges: List[BigRange], wholeRange: String) extends Result

case object NotFoundIt extends Result

case object NonTaken extends Result

case object NonExisting extends Result
