package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import models.MatchRooms
import org.slf4j.{Logger, LoggerFactory}
import routes.Routes.{cors, matchService, settings}
import routes.Utils.handleRequest
import routes.inputs.MatchInputs.PostMatchDTO
import serializers.Json4sSnakeCaseSupport
import services.{ConnectedPlayersService, DeckService, MatchService}


object MatchRoutes extends Json4sSnakeCaseSupport {

  val logger: Logger = LoggerFactory.getLogger(classOf[MatchService])

  def apply(matchService: MatchService,connectionsService: ConnectedPlayersService): Route = {
    concat(
      path("matches") {
        post {
          entity(as[PostMatchDTO]) { postMatchDTO =>
            logger.info(s"[POST] /matches with $postMatchDTO")
            handleRequest(() => {
              val matchId = matchService.createMatch(postMatchDTO.deckId, postMatchDTO.matchCreatorId, postMatchDTO.challengedPlayerId)
              connectionsService.sendMessageToUserId((s"INVITE:${postMatchDTO.challengedPlayerId}:${matchId}"),postMatchDTO.challengedPlayerId)
              matchId.toString
            }, StatusCodes.Created)
          }
        }
      } ~ path("matches" / IntNumber / "result") { matchId =>
        get {
          complete(StatusCodes.OK, s"$matchId result: user1 won")
        }
      } ~ path("matches" / IntNumber / "movements") { matchId =>
        get {
          complete(StatusCodes.OK, s"Match $matchId Movements []: attribute, cards, result")
        }
      } ~ path("matches" / IntNumber / "status") { matchId =>
        patch {
          //BODY status = { FINISHED | IN_PROCESS | PAUSED | CANCELED}
          complete(StatusCodes.NoContent, "Match finished")
        }
      }
    )
  }
}
