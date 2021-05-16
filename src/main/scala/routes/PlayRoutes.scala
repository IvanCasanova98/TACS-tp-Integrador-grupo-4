package routes
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import routes.DeckRoutes.logger
import services.ConnectedPlayersService

object PlayRoutes {

  def apply(service: ConnectedPlayersService): Route = {
    concat(
      pathSingleSlash {
        logger.info("Player joining lobby")
        parameter("userId") { userName =>
          handleWebSocketMessages(service.websocketFlow(userName))
        }
      }
      /*~ path("match/join" / IntNumber) { matchId =>
        parameter("user") { userId =>
          handleWebSocketMessages(MatchRooms.findOrCreate(matchId).websocketFlow(userId))
        }
      }*/
    )
  }

}
