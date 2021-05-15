package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import ch.megard.akka.http.cors.scaladsl.model.{HttpHeaderRange, HttpOriginMatcher}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import serializers.Json4sSnakeCaseSupport
import server.ClassInjection

object Routes extends ClassInjection with Json4sSnakeCaseSupport with CorsDirectives {

  val settings: CorsSettings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)
    .withAllowedOrigins(HttpOriginMatcher.*)
    .withAllowedMethods(Seq(GET, POST, DELETE, OPTIONS, PUT, PATCH))
    .withAllowedHeaders(HttpHeaderRange.*)

  def apply()(implicit actorSystem: ActorSystem): Route = {
    handleRejections(CorsDirectives.corsRejectionHandler) {
      cors(settings) {
        concat(PlayRoutes()
          ~ DeckRoutes(deckService)
          ~ MatchRoutes(matchService)
          ~ LoginRoute()
          ~ CardRoutes()
          ~ StatisticsRoutes()
        )
      }
    }
  }
}


