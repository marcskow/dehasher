package pl.agh.edu.dehaser

import com.typesafe.config.{Config, ConfigFactory}


object Settings {
  val config: Config = ConfigFactory.load("common")

  val queuePath: String = config.getString("app.queuePath")
  val restPort: Int = config.getInt("app.restPort")
  val hostname: String = config.getString("akka.remote.netty.tcp.hostname")

}
