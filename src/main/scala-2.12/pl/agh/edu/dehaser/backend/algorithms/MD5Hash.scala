package pl.agh.edu.dehaser.backend.algorithms

import java.math.BigInteger
import java.security.MessageDigest

class MD5Hash extends HashAlgorithm {
  override def createHash(input: String): String = {
    val md = MessageDigest.getInstance("MD5")
    md.update(input.getBytes("UTF-8"))
    val bytes = md.digest
    String.format("%032x", new BigInteger(1, bytes))
  }
}
