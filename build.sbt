name := "Dehaser_Akka"
version := "1.0"

scalaVersion := "2.12.1"
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

val akkaVersion = "2.4.16"
val akkaHttpVersion = "1.0-RC4"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.12" % "3.0.1" % Test,
  // Akka
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaHttpVersion
)

libraryDependencies += "org.json4s" % "json4s-jackson_2.10" % "3.1.0"



mainClass in(Compile, run) := Some("pl.agh.edu.dehaser.MainSample")
