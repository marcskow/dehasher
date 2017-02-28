package pl.agh.edu.dehaser.utils

import org.json4s.{DefaultFormats, jackson}


trait Json4sFormats {
  implicit val jsonSerialization = jackson.Serialization

  implicit val formats = new DefaultFormats {}
}
