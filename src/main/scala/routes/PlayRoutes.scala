package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.MatchRooms
import routes.DeckRoutes.logger
import services.ConnectedPlayersService

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
      }
    )
  }

}
