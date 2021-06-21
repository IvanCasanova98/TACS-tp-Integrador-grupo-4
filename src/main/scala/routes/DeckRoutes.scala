package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
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
            complete(StatusCodes.OK, deckService.getAll)
        }
      } ~ path("decks") {
        post {
          Utils.authenticated(Utils.adminCheck) { data =>
            logger.info(data.toString())
            entity(as[PartialDeckInput]) { deck =>
              logger.info("[POST] /decks")
              val deckId: Int = deckService.createDeck(deck)
              handleRequest(() => deckId, StatusCodes.Created)
            }
          }
        }
      } ~ path("decks" / IntNumber) { deckId =>
        put {
          Utils.authenticated(Utils.adminCheck) { data =>
            entity(as[PartialDeckInput]) { deck =>
              logger.info(s"[PUT] /decks/$deckId")
              handleRequest(() => deckService.updateDeck(deckId, deck))
            }
          }
        }
      }
        ~ path("decks" / IntNumber) { deckId =>
        delete {
          Utils.authenticated(Utils.adminCheck) { data =>
            logger.info(s"[DELETE] /decks/$deckId")
            handleRequest(() => deckService.deleteDeck(deckId))
          }
        }
      }
    )
  }
}
