package routes

import akka.http.scaladsl.model.HttpMethods.{POST, DELETE, PUT, PATCH, OPTIONS}
import akka.http.scaladsl.model.{HttpMethods, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, get, parameters, patch, path, pathPrefix, post, _}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import ch.megard.akka.http.cors.scaladsl.model.HttpOriginMatcher
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import routes.DeckRoutes.logger
import routes.inputs.LoginInputs.LoginInput
import serializers.Json4sSnakeCaseSupport
import server.ClassInjection
import services.SuperheroApi

object Routes extends ClassInjection with Json4sSnakeCaseSupport with CorsDirectives {

  val settings: CorsSettings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)
    .withAllowedOrigins(HttpOriginMatcher("http://localhost:3000"))
    .withAllowedMethods(Seq(POST, DELETE, OPTIONS, PUT, PATCH))

  def apply(): Route = {
    handleRejections(CorsDirectives.corsRejectionHandler) {
      cors(settings) {
        concat(
          DeckRoutes(deckService)
            ~ MatchRoutes(matchService)
            ~ LoginRoute()
            ~ CardRoutes()
            ~ StatisticsRoutes()
        )
      }
    }
  }
}


