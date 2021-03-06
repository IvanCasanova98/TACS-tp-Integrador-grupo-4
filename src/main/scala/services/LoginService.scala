package services


import com.google.api.client.googleapis.auth.oauth2.{GoogleIdToken, GoogleIdTokenVerifier}
import models.PlayerPermissions
import org.slf4j.{Logger, LoggerFactory}
import repositories.PlayerRepository
import routes.inputs.LoginInputs.LoginInput
import serializers.Json4sSnakeCaseSupport

import scala.collection.mutable


class LoginService(playerRepository: PlayerRepository, verifier: GoogleIdTokenVerifier) extends Json4sSnakeCaseSupport {

  val logger: Logger = LoggerFactory.getLogger(classOf[LoginService])

  def getPlayerPermissions(loginInput: LoginInput): mutable.HashMap[String, String] = {
    var jwt = ""
    val isValidGoogleUser = validatePlayerWithGoogle(loginInput.googleId, loginInput.tokenId)
    if (!isValidGoogleUser) {
      val result = PlayerPermissions(isAuthenticated = false, isAuthorized = false, isAdmin = false)
      jwt = JWTUtils().getJWT(loginInput.googleId, loginInput.tokenId, result.isAuthenticated, result.isAuthorized, result.isAdmin)
    } else {
      val result = playerRepository.getOrCreatePlayerPermissions(loginInput)
      logger.info(s"Found player permissions for playerId '${loginInput.googleId}': $result")
      jwt = JWTUtils().getJWT(loginInput.googleId, loginInput.tokenId, result.isAuthenticated, result.isAuthorized, result.isAdmin)
    }
    mutable.HashMap("access_token" -> jwt)
  }

  //Validate only if the token ID is valid AND belongs to given google ID
  def validatePlayerWithGoogle(googleId: String, tokenId: String): Boolean = {
    val (googleUserInfo, isValidUser): (GoogleIdToken, Boolean) = isValidTokenId(tokenId)

    val googleIdFound = if (isValidUser) googleUserInfo.getPayload.getSubject else ""
    val sameGoogleId = googleId == googleIdFound

    logger.info(s"Found player with Id $googleIdFound, is-valid: '${isValidUser && sameGoogleId}'")
    isValidUser && sameGoogleId
  }

  def isValidTokenId(tokenId: String): (GoogleIdToken, Boolean) = {
    val googleInfo: GoogleIdToken = verifier.verify(tokenId)
    (googleInfo, googleInfo != null)
  }
}
