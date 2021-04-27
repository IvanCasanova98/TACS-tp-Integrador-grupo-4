package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, parameters, patch, path, post, _}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import models.Deck.formats
import serializers.Json4sSnakeCaseSupport
import server.ClassInjection

object Routes extends ClassInjection with Json4sSnakeCaseSupport {

  def apply(): Route = {

    concat(
      path("ping") {
        get {
          complete(StatusCodes.OK, "pong")
        }
      }
        ~ path("login") {
        post {
          complete("")
        }
      }
        ~ DeckRoutes(deckService)
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

