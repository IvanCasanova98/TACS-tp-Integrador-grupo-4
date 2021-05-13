package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import serializers.Json4sSnakeCaseSupport
import services.SuperheroApi

object CardRoutes extends Json4sSnakeCaseSupport {

  def apply(): Route = {

    pathPrefix("cards") {
      concat(
        path(IntNumber / "id") { matchId =>
          get {
            complete(StatusCodes.OK, SuperheroApi().get_hero_by_id(matchId).to_json())
          }
        },
        path(Segment / "name") { matchString =>
          get {
            complete(StatusCodes.OK, SuperheroApi().search_heroes_by_name(matchString).map(card => card.to_json()))
          }
        },
      )
    }
  }
}
