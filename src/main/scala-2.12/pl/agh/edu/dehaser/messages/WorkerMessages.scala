package pl.agh.edu.dehaser.messages

import pl.agh.edu.dehaser.backend.algorithms.HashAlgorithm

import scala.collection.immutable.NumericRange

case class Check(range: NumericRange[BigInt], workDetails: WorkDetails, hasher: HashAlgorithm)

case object WorkAvailable