package routes

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import exceptions.Exceptions.DeckNotFoundException

object Utils {

  def handleRequest[T, S <: StatusCode](tryCatchable: () => T, successCode: S = StatusCodes.NoContent ): StandardRoute = {
    try {
      tryCatchable.apply
      complete(successCode, "")
    } catch {
      case e: DeckNotFoundException => complete(StatusCodes.NotFound, e.getMessage)
      case e: Exception => complete(StatusCodes.InternalServerError, e.getMessage)
    }
  }

}
