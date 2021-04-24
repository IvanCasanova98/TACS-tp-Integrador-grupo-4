package routes

import akka.http.scaladsl.server.Directives.{complete, delete, get, parameters, patch, path, pathPrefix, post, put}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

object Routes {

  def apply(): Route = {
    concat(
      path("asd") {
        get {
          complete(200, "Happy christmas")
        }
      }
        ~ path("login") {
        post {
          complete("")
        }
      }
        ~ pathPrefix("decks") {
        post {
          //BODY: name, card_ids []
          complete(201, "deck created with id")
        }
        path(IntNumber) { deckId =>
          put {
            //BODY: name, card_ids []
            complete(204, s"$deckId")
          }
          delete {
            complete(204, s"Deck deleted $deckId")
          }
        }
      }
        ~ pathPrefix("statistics") {
        parameters("search_by".as[String], "user_id".optional) { (searchBy, userId) =>
          //Query params search match or user
          complete(200, "")
        }
      }
        ~ pathPrefix("matches") {
        post {
          //BODY deck_id, user_ids [], status CREATED
          complete(201, "Match created")
        }
        path(IntNumber / "result") { matchId =>
          get {
            complete(200, s"$matchId result: user1 won")
          }
        }
        path(IntNumber / "movements") { matchId =>
          get {
            complete(200, s"Match $matchId Movements []: attribute, cards, result")
          }
        }
        path(IntNumber / "status") { matchId =>
          patch {
            //BODY status = { FINISHED | IN_PROCESS | PAUSED | CANCELED}
            complete(204, "Match finished")
          }
        }
        parameters("user_id".as[String]) { (userId) =>
          complete(200, "")
        }
      }
    )
  }
}

