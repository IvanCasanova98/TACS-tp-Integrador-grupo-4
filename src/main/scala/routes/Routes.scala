package routes

import akka.http.scaladsl.model.HttpMethods.{DELETE, GET, OPTIONS, PATCH, POST, PUT}
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, get, parameters, patch, path, pathPrefix, post, _}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import ch.megard.akka.http.cors.scaladsl.model.{HttpHeaderRange, HttpOriginMatcher}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import routes.DeckRoutes.logger
import routes.inputs.LoginInputs.LoginInput
import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl.Flow
import serializers.Json4sSnakeCaseSupport
import server.ClassInjection

object Routes extends ClassInjection with Json4sSnakeCaseSupport with CorsDirectives {

  val settings: CorsSettings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)
    .withAllowedOrigins(HttpOriginMatcher.*)
    .withAllowedMethods(Seq(GET, POST, DELETE, OPTIONS, PUT, PATCH))
    .withAllowedHeaders(HttpHeaderRange.*)

  val echoService: Flow[Message, Message, _] = Flow[Message].map {
    case TextMessage.Strict(txt) => TextMessage("ECHO: " + txt)
    case _ => TextMessage("Message type unsupported")
  }

  def apply(): Route = {
    handleRejections(CorsDirectives.corsRejectionHandler) {
        cors(settings) {
          concat(
            pathSingleSlash {
              logger.info("SOCKET MESSAGE ARRIVED")
              handleWebSocketMessages(echoService)
            }
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


