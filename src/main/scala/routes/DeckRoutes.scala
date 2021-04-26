package routes

import akka.http.scaladsl.server.Directives.{as, complete, concat, delete, entity, get, path, post, put}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import org.json4s.{DefaultFormats, Formats}
import org.slf4j.{Logger, LoggerFactory}
import routes.inputs.DeckInputs.PostDeckInput
import serializers.Json4sSnakeCaseSupport
import services.DeckService

object DeckRoutes extends Json4sSnakeCaseSupport {

  implicit val fm: Formats = DefaultFormats
  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def apply(deckService: DeckService): Route = {
    path("decks") {
      concat(
        post {
          entity(as[PostDeckInput]) { deck =>
            logger.info(s"[POST] /decks arrived with name: ${deck.name}")
            val deckId: Int = deckService.createDeck(deck)
            complete(201, s"Deck created with id: $deckId")
          }
        },
        path(IntNumber) { deckId =>
          put {
            //BODY: name, card_ids []
            complete(204, s"$deckId")
          }
          delete {
            complete(204, s"Deck deleted $deckId")
          }
        }
      )
    }
  }

}
