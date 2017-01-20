name := "Dehaser_Akka"
version := "1.0"

scalaVersion := "2.12.1"

val akkaVersion = "2.4.16"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.12" % "3.0.1" % Test,
  // Akka
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  //  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion //,
  //  "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion

  //,"com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  //"com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion
  //"com.typesafe.akka" %% "akka-remote" % akkaVersion,
)




mainClass in(Compile, run) := Some("pl.agh.edu.dehaser.MainSample")
