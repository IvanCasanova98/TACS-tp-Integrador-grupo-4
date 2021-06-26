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
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "com.google.api-client" % "google-api-client" % "1.31.5",
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "org.mockito" %% "mockito-scala" % "1.5.12" % Test,
  "com.github.tomakehurst" % "wiremock" % "2.18.0" % Test,
  "com.h2database" % "h2" % "1.4.200" % Test,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.3",
  "mysql" % "mysql-connector-java" % "8.0.12"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
