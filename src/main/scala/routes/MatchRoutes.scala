package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.{Logger, LoggerFactory}
import routes.Utils.handleRequest
import routes.inputs.MatchInputs.{PostMatchDTO, UpdateMatchStatus}
import serializers.Json4sSnakeCaseSupport
import services.{ConnectedPlayersService, MatchService}


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
              connectionsService.sendMessageToUserId(s"INVITE:${postMatchDTO.challengedPlayerId}:$matchId",postMatchDTO.challengedPlayerId)
              matchId
            }, StatusCodes.Created)
          }
        }
      } ~ path("matches" / IntNumber) { matchId =>
        get {
          Utils.authenticated(Utils.matchIdCheck(matchId)) { data =>
            handleRequest(() => matchService.findMatchById(matchId), StatusCodes.OK)
          }
        }
      } ~ path("matches") {
        get {
            parameters("user_id") { userId =>
              Utils.authenticated(Utils.userIdCheck(userId)) { data =>
              logger.info(s"[GET] /matches for user $userId")
              handleRequest(() => matchService.findMatchesOfUser(userId), StatusCodes.OK)
            }
          }
        }
      } ~ path("matches" / IntNumber / "result") { matchId =>
        get {
          Utils.authenticated(Utils.matchIdCheck(matchId)) { data =>
            complete(StatusCodes.OK, s"$matchId result: user1 won")
          }
        }
      } ~ path("matches" / IntNumber / "status") { matchId =>
        patch {
          //BODY status = { FINISHED | IN_PROCESS | PAUSED | CANCELED}
          Utils.authenticated(Utils.matchIdCheck(matchId)) { data =>
            entity(as[UpdateMatchStatus]) { newStatusDTO =>
              complete(StatusCodes.NoContent, matchService.updateMatchStatus(matchId, newStatusDTO.status))
            }
          }
        }
      }
    )
  }
}
