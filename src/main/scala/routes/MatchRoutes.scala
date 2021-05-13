package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import org.slf4j.{Logger, LoggerFactory}
import routes.Routes.{cors, settings}
import serializers.Json4sSnakeCaseSupport
import services.DeckService

object MatchRoutes extends Json4sSnakeCaseSupport {

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def apply(): Route = {
    handleRejections(CorsDirectives.corsRejectionHandler) {
      cors(settings) {
        concat(
          path("matches") {
            concat(
              post {
                //BODY deck_id, user_ids [], status CREATED
                complete(StatusCodes.Created, "Match created")
              },
              path(IntNumber / "result") { matchId =>
                get {
                  complete(StatusCodes.OK, s"$matchId result: user1 won")
                }
              },
              path(IntNumber / "movements") { matchId =>
                get {
                  complete(StatusCodes.OK, s"Match $matchId Movements []: attribute, cards, result")
                }
              },
              path(IntNumber / "status") { matchId =>
                patch {
                  //BODY status = { FINISHED | IN_PROCESS | PAUSED | CANCELED}
                  complete(StatusCodes.NoContent, "Match finished")
                }
              },
              parameters("user_id".as[String]) { (userId) =>
                complete(StatusCodes.OK, "")
              }
            )
          }
        )
      }
    }
  }
}
