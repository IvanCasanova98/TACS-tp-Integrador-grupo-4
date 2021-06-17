package routes

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, provide}
import akka.http.scaladsl.server.{Directive1, StandardRoute}
import exceptions.Exceptions.DeckNotFoundException
import io.really.jwt.{JWT, JWTResult}
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

  private def isTokenExpired(jwt: String): Boolean =
    getClaims(jwt).get("exp").exists(_.toLong < System.currentTimeMillis())

  private def getClaims(jwt: String): Map[String, String] =
    JWT.decode(jwt, Some("secret-key")).asInstanceOf[JWTResult.JWT].payload.as[Map[String, String]]

  def authenticated: Directive1[Map[String, Any]] = {

    optionalHeaderValueByName("Authorization").flatMap { tokenFromUser =>
      if (tokenFromUser.isEmpty){ complete(StatusCodes.Unauthorized ->"Invalid Token")}
      val jwtToken = tokenFromUser.get.split(" ")
      if (jwtToken.size<1){complete(StatusCodes.Unauthorized ->"Invalid Token")}
      val token_decode = JWT.decode(jwtToken(1), Some("secret-key"))
      val correct = (token_decode == JWTResult.EmptyJWT || token_decode == JWTResult.InvalidSignature ||token_decode == JWTResult.InvalidHeader || token_decode == JWTResult.TooManySegments || token_decode == JWTResult.NotEnoughSegments)
      jwtToken(1) match {
        case token if correct =>
          complete(StatusCodes.Unauthorized ->"Invalid Token")
        case token if isTokenExpired(token) =>
          complete(StatusCodes.Unauthorized -> "Session expired.")
        case token if token_decode.isInstanceOf[JWTResult.JWT] && !correct=>
          provide(getClaims(token))

        case _ =>  complete(StatusCodes.Unauthorized ->"Invalid Token")
      }
    }
  }
}
