package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.json4s.{DefaultFormats, Formats}
import org.slf4j.{Logger, LoggerFactory}
import routes.Utils.handleRequest
import routes.inputs.DeckInputs.PartialDeckInput
import serializers.Json4sSnakeCaseSupport
import services.DeckService

object DeckRoutes extends Json4sSnakeCaseSupport {

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def apply(deckService: DeckService): Route = {
    concat(
      path("decks") {
        get {
          logger.info("[GET] /decks")
          complete(StatusCodes.OK, deckService.getAll)
        }
      },
      path("decks") {
        post {
          entity(as[PartialDeckInput]) { deck =>
            logger.info("[POST] /decks")
            val deckId: Int = deckService.createDeck(deck)
            handleRequest(() => s"Deck created with id: $deckId", StatusCodes.Created)
          }
        }
      },
      path("decks" / IntNumber) { deckId =>
        put {
          entity(as[PartialDeckInput]) { deck =>
            logger.info(s"[PUT] /decks/$deckId")
            handleRequest(() => deckService.updateDeck(deckId, deck))
          }
        }
      },
      path("decks" / IntNumber) { deckId =>
        delete {
          logger.info(s"[DELETE] /decks/$deckId")
          handleRequest(() => deckService.deleteDeck(deckId))
        }
      }
    )
  }

}
