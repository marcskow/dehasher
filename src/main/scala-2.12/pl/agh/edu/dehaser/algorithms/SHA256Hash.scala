package pl.agh.edu.dehaser.algorithms

import java.math.BigInteger
import java.security.MessageDigest


class SHA256Hash extends HashAlgorithm {
  override def createHash(input: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(input.getBytes("UTF-8"))
    val bytes = md.digest
    String.format("%064x", new BigInteger(1, bytes))
  }
}
