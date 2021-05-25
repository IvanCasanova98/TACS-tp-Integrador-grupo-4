package routes

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import exceptions.Exceptions
import exceptions.Exceptions.DeckNotFoundException
import serializers.Json4sSnakeCaseSupport


object Utils extends Json4sSnakeCaseSupport {


  def handleRequest[T <:Any, S <: StatusCode](tryCatchable: () => T, successCode: S = StatusCodes.NoContent ): StandardRoute = {
    try {
      val result = tryCatchable.apply
      complete(successCode, result)
    } catch {
      case e: DeckNotFoundException => complete(StatusCodes.NotFound, e.getMessage)
      case e: Exception => complete(StatusCodes.InternalServerError, e.getMessage)
    }
  }

}
