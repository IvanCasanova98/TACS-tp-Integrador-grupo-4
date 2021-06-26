package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.{Logger, LoggerFactory}
import repositories.PlayerRepository
import routes.Utils.handleRequest
import serializers.Json4sSnakeCaseSupport

object PlayerRoutes extends Json4sSnakeCaseSupport {
  val logger: Logger = LoggerFactory.getLogger(classOf[PlayerRepository])

  def apply(playerRepository: PlayerRepository): Route = {
    concat(
      path("players" / Segment) { userId =>
        get {
          logger.info(s"[GET] /players/$userId")
          handleRequest(() => playerRepository.getPlayerById(userId), StatusCodes.OK)
        }
      }
    )
  }

}
