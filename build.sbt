name := "tacs-tp-integrador-grupo-4"

version := "0.1"

scalaVersion := "2.13.5"

mainClass in Compile := Some("server.HttpServer")

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "org.slf4j" % "slf4j-api" % "1.7.25"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
