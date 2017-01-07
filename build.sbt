name := "Dehaser_Akka"
version := "1.0"

scalaVersion := "2.12.1"

val akkaVersion = "2.4.16"

libraryDependencies ++= Seq(
  // Change this to another test framework if you prefer
  "org.scalatest" % "scalatest_2.12" % "3.0.1",
  // Akka
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  //"com.typesafe.akka" %% "akka-remote" % akkaVersion,
)
