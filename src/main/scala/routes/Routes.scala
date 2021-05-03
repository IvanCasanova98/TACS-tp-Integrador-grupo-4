package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, parameters, patch, path, pathPrefix, post, _}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.{RejectionHandler, Route}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import routes.DeckRoutes.logger
import routes.inputs.LoginInputs.LoginInput
import serializers.Json4sSnakeCaseSupport
import server.ClassInjection
import services.SuperheroApi

object Routes extends ClassInjection with Json4sSnakeCaseSupport with CorsDirectives {

  val settings: CorsSettings = CorsSettings.defaultSettings.withAllowGenericHttpRequests(true)

  def apply(): Route = {
    handleRejections(CorsDirectives.corsRejectionHandler) {
      cors(settings) {
        concat(
          DeckRoutes(deckService)
          ~ handleRejections(RejectionHandler.default) {
            path("login") {
              post {
                entity(as[LoginInput]) { loginInput =>
                  logger.info(s"[POST] /login with: $loginInput")
                  complete(StatusCodes.OK, loginService.getPlayerPermissions(loginInput))
                }
              }
            }
          }
          ~  pathPrefix("cards") {
            concat(
              path(IntNumber / "id"){ matchId =>
                get {
                  complete(StatusCodes.OK, SuperheroApi().get_hero_by_id(matchId).to_json())
                }
              },
              path(Segment /  "name"){ matchString =>
                get {
                  complete(StatusCodes.OK, SuperheroApi().search_heroes_by_name(matchString).map(card => card.to_json()))
                }
              },
            )
          }
            ~ path("statistics") {
            parameters("search_by".as[String], "user_id".optional) { (searchBy, userId) =>
              //Query params search match or user
              complete(StatusCodes.OK, "")
            }
          }
            ~ path("matches") {
            concat(
              post {
                //BODY deck_id, user_ids [], status CREATED
                complete(StatusCodes.Created, "Match created")
              },
              path(IntNumber / "result") { matchId =>
                get {
                  complete(StatusCodes.OK, s"$matchId result: user1 won")
                }
              },
              path(IntNumber / "movements") { matchId =>
                get {
                  complete(StatusCodes.OK, s"Match $matchId Movements []: attribute, cards, result")
                }
              },
              path(IntNumber / "status") { matchId =>
                patch {
                  //BODY status = { FINISHED | IN_PROCESS | PAUSED | CANCELED}
                  complete(StatusCodes.NoContent, "Match finished")
                }
              },
              parameters("user_id".as[String]) { (userId) =>
                complete(StatusCodes.OK, "")
              }
            )
          }
        )
      }
    }
  }
}


