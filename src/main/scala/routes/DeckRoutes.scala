package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.json4s.{DefaultFormats, Formats}
import org.slf4j.{Logger, LoggerFactory}
import routes.inputs.DeckInputs.PartialDeckInput
import serializers.Json4sSnakeCaseSupport
import services.DeckService

object DeckRoutes extends Json4sSnakeCaseSupport {

  implicit val fm: Formats = DefaultFormats
  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def apply(deckService: DeckService): Route = {
    concat(
      path("decks") {
        post {
          entity(as[PartialDeckInput]) { deck =>
            logger.info("[POST] /decks")
            val deckId: Int = deckService.createDeck(deck)
            complete(StatusCodes.Created, s"Deck created with id: $deckId")
          }
        }
      },
      path("decks" / IntNumber) { deckId =>
        put {
          entity(as[PartialDeckInput]) { deck =>
            logger.info(s"[PUT] /decks/$deckId")

            deckService.updateDeck(deckId, deck)
            complete(StatusCodes.NoContent, s"$deckId")
          }
        }
      },
      path("decks" / IntNumber) { deckId =>
        delete {
          logger.info(s"[DELETE] /decks/$deckId")
          val deleted = deckService.deleteDeck(deckId)
          val statusCode = if (deleted) StatusCodes.NoContent else StatusCodes.NotFound
          complete(statusCode, s"Deck id $deckId was not found")
        }
      }
    )
  }

}
