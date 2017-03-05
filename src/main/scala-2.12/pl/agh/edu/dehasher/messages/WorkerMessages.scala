package pl.agh.edu.dehasher.messages

import scala.collection.immutable.NumericRange

case class Check(range: NumericRange[BigInt], workDetails: WorkDetails)

case object WorkAvailable