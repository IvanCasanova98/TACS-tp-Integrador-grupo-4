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

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def apply(statisticsService: StatisticsService): Route = {
    concat(
    path("statistics" / "rankings") {
      handleRequest(() => statisticsService.getRanking, StatusCodes.OK)
    }
    ~ path("statistics") {
      parameters("user_id".optional, "from_date".optional, "to_date".optional) { (userId, fromDate, toDate) =>
        statisticsService.getMatchesStatistics(userId, fromDate.map(Date.valueOf), toDate.map(Date.valueOf))
        complete(StatusCodes.OK, "")
      }
    }
    )
  }
}
