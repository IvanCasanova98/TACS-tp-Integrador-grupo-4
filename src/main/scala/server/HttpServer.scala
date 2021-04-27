package server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import routes.Routes

import scala.concurrent.ExecutionContextExecutor

object HttpServer {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "tacs-tp")

    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val route = Routes()

    val futureBinding = Http().newServerAt("0.0.0.0", 9000).bind(route)

    println(s"Server online at http://localhost:9000/")

    futureBinding.flatMap(_.unbind())
  }
}