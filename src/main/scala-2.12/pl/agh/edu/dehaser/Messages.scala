package pl.agh.edu.dehaser

import scala.collection.immutable.NumericRange


sealed trait CheckResponse

case class DehashIt(hash: String, algo: String)

case class Check(range: NumericRange[BigInt], hash: String, algo: String)

// TODO: send original hash and algo or not?
case class FoundIt(crackedPass: String) extends CheckResponse

case class RangeChecked(range: NumericRange[BigInt]) extends CheckResponse
