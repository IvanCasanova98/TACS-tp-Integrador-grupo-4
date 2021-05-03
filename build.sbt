name := "tacs-tp-integrador-grupo-4"

version := "0.1"

scalaVersion := "2.13.5"

mainClass in Compile := Some("server.HttpServer")

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.2"
val vJson4s = "3.7.0-M16"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.9.6",
  "org.json4s" %% "json4s-jackson" % vJson4s,
  "org.json4s" %% "json4s-native" % vJson4s,
  "ch.megard" %% "akka-http-cors" % "1.1.1",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
