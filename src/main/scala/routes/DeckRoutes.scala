package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, Route}
import org.slf4j.{Logger, LoggerFactory}
import routes.Routes.{cors, settings}
import routes.Utils.handleRequest
import routes.inputs.DeckInputs.PartialDeckInput
import serializers.Json4sSnakeCaseSupport
import services.DeckService

object DeckRoutes extends Json4sSnakeCaseSupport {

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def apply(deckService: DeckService): Route = {
    cors(settings) {
      concat(
        path("decks") {
          get {
            logger.info("[GET] /decks")
            complete(StatusCodes.OK, deckService.getAll)
          }
        }
        ,
        path("decks") {
          handleRejections(RejectionHandler.default) {
            post {
              entity(as[PartialDeckInput]) { deck =>
                logger.info("[POST] /decks")
                val deckId: Int = deckService.createDeck(deck)
                handleRequest(() => s"Deck created with id: $deckId", StatusCodes.Created)
              }
            }
          }
        }
        ,
        path("decks" / IntNumber) { deckId =>
          handleRejections(RejectionHandler.default) {
            put {
              entity(as[PartialDeckInput]) { deck =>
                logger.info(s"[PUT] /decks/$deckId")
                handleRequest(() => deckService.updateDeck(deckId, deck))
              }
            }
          }
        }
        ,
        path("decks" / IntNumber) { deckId =>
          handleRejections(RejectionHandler.default) {
            delete {
              logger.info(s"[DELETE] /decks/$deckId")
              complete(StatusCodes.NoContent, deckService.deleteDeck(deckId))
            }
          }
        }
      )
    }
  }

}
