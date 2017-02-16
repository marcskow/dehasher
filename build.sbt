name := "Dehaser_Akka"
version := "1.0"

scalaVersion := "2.12.1"

val akkaVersion = "2.4.16"
val akkaHttp = "10.0.0"
val json4s = "3.5.0"
resolvers += Resolver.bintrayRepo("hseeberger", "maven")


libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.12" % "3.0.1" % Test,
  "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.1",
  // Akka
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,

  "com.typesafe.akka" %% "akka-http-core" % akkaHttp,
  "com.typesafe.akka" %% "akka-http" % akkaHttp,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttp,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

  "org.json4s" %% "json4s-jackson" % json4s,
  "org.json4s" %% "json4s-ext" % json4s,

  "de.heikoseeberger" %% "akka-http-json4s" % "1.12.0" exclude("org.json4s", "json4s-core_2.11")
)

mainClass in(Compile, run) := Some("pl.agh.edu.dehaser.MainSample")
