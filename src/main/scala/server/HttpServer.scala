package server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import routes.Routes

import scala.concurrent.ExecutionContextExecutor

object HttpServer {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")

    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val route = {
      Routes()
    }

    Http().newServerAt("0.0.0.0", 8080).bind(route)

    println(s"Server online at http://localhost:8080/")

  }
}