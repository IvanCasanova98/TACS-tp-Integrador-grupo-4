package routes

import akka.http.scaladsl.server.Directives.{complete, get, parameters, patch, path, pathPrefix, post, _}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import models.Deck.formats
import org.json4s.{DefaultFormats, Formats}
import routes.inputs.DeckInputs.PostDeckInput
import serializers.Json4sSnakeCaseSupport
import server.ClassInjection

object Routes extends ClassInjection with Json4sSnakeCaseSupport {

  def apply(): Route = {

    concat(
      path("ping") {
        get {
          complete(200, "pong")
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
          complete(200, "")
        }
      }
        ~ path("matches") {
        concat(
        post {
          //BODY deck_id, user_ids [], status CREATED
          complete(201, "Match created")
        },
        path(IntNumber / "result") { matchId =>
          get {
            complete(200, s"$matchId result: user1 won")
          }
        },
        path(IntNumber / "movements") { matchId =>
          get {
            complete(200, s"Match $matchId Movements []: attribute, cards, result")
          }
        },
        path(IntNumber / "status") { matchId =>
          patch {
            //BODY status = { FINISHED | IN_PROCESS | PAUSED | CANCELED}
            complete(204, "Match finished")
          }
        },
        parameters("user_id".as[String]) { (userId) =>
          complete(200, "")
        }
        )
      }
    )
  }
}

