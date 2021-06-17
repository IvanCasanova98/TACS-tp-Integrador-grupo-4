package routes

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import exceptions.Exceptions.{DeckNotFoundException, InvalidQueryParamsException}
import org.joda.time.Seconds
import serializers.Json4sSnakeCaseSupport

import scala.io.Source
import scala.util.Random


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
   * Method to get random item of collection
   * @param collection of type T
   * @return random element of type T
   * */
  def getRandomItemOfSeq[T](collection: Seq[T]): T = {
    if (collection.length <= 1) return collection.head
    collection(new Random().nextInt(collection.length))
  }

  def delayExecution(seconds: Int): Unit = Thread.sleep(seconds * 1000)

  /**
   * Method to read file from resources
   * @param name: name of file to read from resources
   * @return
   */
  def resource(name: String): String =
    Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(name), "UTF-8").mkString

}
