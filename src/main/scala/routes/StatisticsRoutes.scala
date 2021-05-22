package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.{Logger, LoggerFactory}
import serializers.Json4sSnakeCaseSupport
import services.DeckService

object StatisticsRoutes extends Json4sSnakeCaseSupport {

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def apply(): Route = {
    path("statistics") {
      parameters("search_by".as[String], "user_id".optional) { (searchBy, userId) =>
        //Query params search match or user
        complete(StatusCodes.OK, "")
      }
    }
  }
}
