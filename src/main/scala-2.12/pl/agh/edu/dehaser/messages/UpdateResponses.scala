package pl.agh.edu.dehaser.messages

import pl.agh.edu.dehaser.BigRange

sealed trait Result

case class Cracked(dehashed: String) extends Result

case class Ranges(ranges: List[BigRange]) extends Result

case object NotFoundIt extends Result


case object NonTaken extends Result

case object NonExisting extends Result
