package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import services.{ConnectedPlayersService, MatchRooms}

object PlayRoutes {

  def apply(connectedPlayersService: ConnectedPlayersService, matchRooms: MatchRooms): Route = {
    concat(
      path("home") {
        parameter("userId") { userId =>
          handleWebSocketMessages(connectedPlayersService.websocketFlow(userId))
        }
      } ~ path("join-match" / IntNumber) { matchId =>
        parameter("userId") { userId =>
          handleWebSocketMessages(matchRooms.findOrCreate(matchId).websocketFlow(userId))
        }
      } ~ path("invite" / IntNumber / Segment) { (matchId, invitedUserId) =>
        connectedPlayersService.sendMessageToUserId(s"INVITE:$invitedUserId:$matchId", invitedUserId)
        complete(StatusCodes.OK, s"Invited user $invitedUserId")
      }
    )
  }

}
