package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.{Directive0, Rejection, RejectionHandler}
import akka.http.scaladsl.server.Directives.{complete, extractRequest, handleRejections, mapResponse, reject}
import routes.Routes

import java.util.concurrent.atomic.AtomicInteger

object HttpServer {
  case class PathBusyRejection(path: Uri.Path, max: Int) extends Rejection

  class Limiter(max: Int) {
    val concurrentRequests = new AtomicInteger(0)

    val limitConcurrentRequests: Directive0 =
      extractRequest.flatMap { request =>
        if (concurrentRequests.incrementAndGet() > max) {
          concurrentRequests.decrementAndGet()
          reject(PathBusyRejection(request.uri.path, max))
        } else {
          mapResponse { response =>
            concurrentRequests.decrementAndGet()
            response
          }
        }

      }
  }
  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("tacs-tp")

    val rejectionHandler = RejectionHandler.newBuilder()
      .handle {
        case PathBusyRejection(path, max) =>
          complete((StatusCodes.EnhanceYourCalm, s"Max concurrent requests for $path reached, please try again later"))
      }.result()

    val limiter = new Limiter(max = 3)

    val route =  handleRejections(rejectionHandler) {
      limiter.limitConcurrentRequests {
        Routes()
      }
    }

    Http().newServerAt("0.0.0.0", 9000).bind(route)
    println(s"Server online at http://localhost:9000/")

  }
}