package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directives.{complete, extractRequest, handleRejections, mapResponse, reject}
import akka.http.scaladsl.server.{Directive0, Rejection, RejectionHandler}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import ch.megard.akka.http.cors.scaladsl.model.{HttpHeaderRange, HttpOriginMatcher}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import routes.Routes

import java.util.concurrent.atomic.AtomicInteger

object HttpServer extends CorsDirectives {
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

    val settings: CorsSettings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)
      .withAllowedOrigins(HttpOriginMatcher.*)
      .withAllowedMethods(Seq(GET, POST, DELETE, OPTIONS, PUT, PATCH))
      .withAllowedHeaders(HttpHeaderRange.*)

    val rejectionHandler = RejectionHandler.newBuilder()
      .handle {
        case PathBusyRejection(path, max) =>
          complete((StatusCodes.EnhanceYourCalm, s"Max concurrent requests for $path reached, please try again later"))
      }.result()

    val limiter = new Limiter(max = 3)

    val route =  handleRejections(CorsDirectives.corsRejectionHandler) {
      cors(settings) {
        handleRejections(rejectionHandler) {
          limiter.limitConcurrentRequests {
            Routes()
          }
        }
      }
    }

    Http().newServerAt("0.0.0.0", 9000).bind(route)
    println(s"Server online at http://localhost:9000/")

  }
}