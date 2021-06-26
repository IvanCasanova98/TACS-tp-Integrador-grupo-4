package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import serializers.Json4sSnakeCaseSupport
import services.SuperheroApi

object CardRoutes extends Json4sSnakeCaseSupport {

  def apply(): Route = {
    concat(
      pathPrefix("cards") {
        path(IntNumber / "id") { matchId =>
          get {
            complete(StatusCodes.OK, SuperheroApi().getHeroById(matchId).to_json())
          }
        } ~ path(Segment / "name") { matchString =>
          get {
            complete(StatusCodes.OK, SuperheroApi().searchHeroesByName(matchString).map(card => card.to_json()))
          }
        }
      }
    )
  }
}
