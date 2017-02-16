package pl.agh.edu.dehaser.modules

import org.json4s.{Extraction, CustomSerializer}
import org.json4s.JsonAST.{JObject, JValue}

/**
  * Created by razakroner on 2017-02-16.
  */
class TaskSerializer extends CustomSerializer[Task](implicit format => ({
  case m: JValue =>
    val id = (m \ "id").extract[String]
    val hash = (m \ "hash").extract[String]
    Task(id, hash)
},{
  case Task(id, hash) =>
    JObject("id" -> Extraction.decompose(id), "hash" -> Extraction.decompose(hash))
}))
