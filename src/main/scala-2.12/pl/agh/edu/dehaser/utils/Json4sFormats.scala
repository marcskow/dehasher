package pl.agh.edu.dehaser.utils

import org.json4s.{DefaultFormats, jackson}

/**
  * Created by razakroner on 2017-02-16.
  */
trait Json4sFormats {
  implicit val jsonSerialization = jackson.Serialization

  implicit val formats = new DefaultFormats {}
}
