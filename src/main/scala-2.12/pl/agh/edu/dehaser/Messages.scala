package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange


sealed trait CheckResponse

case class DehashIt(hash: String, algo: String)

case class Check(range: NumericRange[BigInt], workDetails: WorkDetails)

case class CheckHalf(range: BigRange, hash: String, algo: String)

case class WorkDetails(hash: String, algo: String)

case object GiveHalf

// TODO: send original hash and algo or not?
case class FoundIt(crackedPass: String) extends CheckResponse

case class RangeChecked(range: NumericRange[BigInt]) extends CheckResponse
