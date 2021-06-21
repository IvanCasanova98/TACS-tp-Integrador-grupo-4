package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.{Logger, LoggerFactory}
import routes.Utils.handleRequest
import serializers.Json4sSnakeCaseSupport
import services.{DeckService, StatisticsService}

import java.sql.Date

object StatisticsRoutes extends Json4sSnakeCaseSupport {

  val logger: Logger = LoggerFactory.getLogger(classOf[StatisticsService])

  def apply(statisticsService: StatisticsService): Route = {
    concat(
      path("statistics" / "rankings") {
        Utils.authenticated(Utils.adminCheck) { data =>
          logger.info("[GET] /statistics/rankings")
          handleRequest(() => statisticsService.getRanking, StatusCodes.OK)
        }
      }
        ~ path("statistics") {
        Utils.authenticated(Utils.adminCheck) { data =>
          parameters("user_id".optional, "from_date".optional, "to_date".optional) { (userId, fromDate, toDate) =>
            logger.info(s"[GET] /statistics $userId, $fromDate, $toDate")
            handleRequest(() => statisticsService.getMatchesStatistics(userId, fromDate.map(Date.valueOf), toDate.map(Date.valueOf)), StatusCodes.OK)
          }
        }
      }
    )
  }
}
