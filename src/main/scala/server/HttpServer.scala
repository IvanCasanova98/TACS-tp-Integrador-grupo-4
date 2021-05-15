package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import routes.Routes

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object HttpServer {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("tacs-tp")

    val route = Routes()

    Http().newServerAt("0.0.0.0", 9000).bind(route)

    println(s"Server online at http://localhost:9000/")

  }
}