package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, path, post}
import akka.http.scaladsl.server.{Directives, Route}
import routes.DeckRoutes.logger
import routes.Routes.loginService
import routes.inputs.LoginInputs.LoginInput
import serializers.Json4sSnakeCaseSupport

object LoginRoute extends Json4sSnakeCaseSupport {
  def apply(): Route = {
    Directives.concat(
      path("login") {
        post {
          entity(as[LoginInput]) { loginInput =>
            logger.info(s"[POST] /login with: $loginInput")
            complete(StatusCodes.OK, loginService.getPlayerPermissions(loginInput))
          }
        }
      })
  }
}
