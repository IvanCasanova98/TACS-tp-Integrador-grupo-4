package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import serializers.Json4sSnakeCaseSupport
import server.ClassInjection
import services.{ConnectedPlayersService, MatchRooms}

object Routes extends ClassInjection with Json4sSnakeCaseSupport {

  def apply()(implicit actorSystem: ActorSystem): Route = {
    implicit val materializer: Materializer = Materializer.matFromSystem
    val connectionsService = new ConnectedPlayersService(actorSystem, playerRepository)
    val matchRooms = new MatchRooms(actorSystem, matchService, jsonParser)

        concat(PlayRoutes(connectionsService, matchRooms)
          ~ DeckRoutes(deckService)
          ~ MatchRoutes(matchService,connectionsService)
          ~ LoginRoute()
          ~ CardRoutes()
          ~ StatisticsRoutes(statisticsService)
          ~ PlayerRoutes(playerRepository)
        )
      }
}


