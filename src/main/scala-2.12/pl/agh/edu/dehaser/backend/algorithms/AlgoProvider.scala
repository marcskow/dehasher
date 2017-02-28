package pl.agh.edu.dehaser.backend.algorithms

object AlgoProvider {
  def getAlgorithm(algoType: String): HashAlgorithm = algoType match {
    case "SHA-1" => new SHA1Hash()
    case "SHA-256" => new SHA256Hash()
    case "MD5" => new MD5Hash()
    case _ => throw new RuntimeException("Unsupported algorithm type")
  }

}
