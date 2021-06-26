package routes

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import exceptions.Exceptions.{DeckNotFoundException, InvalidQueryParamsException}
import serializers.Json4sSnakeCaseSupport

import scala.io.Source


object Utils extends Json4sSnakeCaseSupport {

  /**
   * Method to avoid exception handling in routes and status codes
   * @param tryCatchable function that resolves the request
   * @param successCode success code that should be in the response (200, 201)
   * @tparam T function that can be try catched
   * @tparam S status code from StatusCodes
   * @return
   */
  def handleRequest[T <:Any, S <: StatusCode](tryCatchable: () => T, successCode: S = StatusCodes.NoContent ): StandardRoute = {
    try {
      val result = tryCatchable.apply
      complete(successCode, result)
    } catch {
      case e: DeckNotFoundException => complete(StatusCodes.NotFound, e.getMessage)
      case e: InvalidQueryParamsException => complete(StatusCodes.BadRequest, e.getMessage)
      case e: Exception => complete(StatusCodes.InternalServerError, e.getMessage)
    }
  }


  /**
   * Method to read file from resources
   * @param name: name of file to read from resources
   * @return
   */
  def resource(name: String): String =
    Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(name), "UTF-8").mkString

}
