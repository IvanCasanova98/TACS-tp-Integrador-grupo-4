package services


import models.{Player, PlayerPermissions}
import org.slf4j.{Logger, LoggerFactory}
import repositories.PlayerRepository
import routes.inputs.LoginInputs.LoginInput
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import serializers.Json4sSnakeCaseSupport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import services.JWTUtils

import java.util.Collections
import scala.collection.mutable


class LoginService(playerRepository: PlayerRepository) extends Json4sSnakeCaseSupport {
  //GOOGLE SIGN IN VERIFIER
  val verifier: GoogleIdTokenVerifier =
    new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
      // Specify the CLIENT_ID of the app that accesses the backend:
      .setAudience(Collections.singletonList("1058494963753-drauquf06tsu1jnbl7k13ptrp98s323d.apps.googleusercontent.com"))
      // Or, if multiple clients access the backend:
      //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
      .build()

  val logger: Logger = LoggerFactory.getLogger(classOf[LoginService])

  def getPlayerPermissions(loginInput: LoginInput): mutable.HashMap[String, String] = {
    var jwt = ""
    val isValidGoogleUser = validatePlayerWithGoogle(loginInput.googleId,loginInput.tokenId)
    if (!isValidGoogleUser){
      val result = PlayerPermissions(isAuthenticated = false,isAuthorized = false,isAdmin = false)
      jwt = JWTUtils().getJWT(loginInput.googleId, loginInput.tokenId, result.isAuthenticated, result.isAuthorized, result.isAdmin)
    }else {

      val result = playerRepository.getOrCreatePlayerPermissions(loginInput)
      logger.info(s"Found player permissions for playerId '${loginInput.googleId}': $result")
      jwt = JWTUtils().getJWT(loginInput.googleId, loginInput.tokenId, result.isAuthenticated, result.isAuthorized, result.isAdmin)
    }
    mutable.HashMap("access_token" -> jwt)
  }

  //Validate only if the token ID is valid AND belongs to given google ID
  def validatePlayerWithGoogle(googleId: String, tokenId: String): Boolean = {
    val (googleUserInfo,isValidUser): (GoogleIdToken,Boolean) = isValidTokenId(tokenId)

    val googleIdFound = if (isValidUser) googleUserInfo.getPayload.getSubject else ""
    val sameGoogleId = googleId == googleIdFound

    logger.info(s"Found player with Id $googleIdFound, is-valid: '${isValidUser && sameGoogleId}'")
    isValidUser && sameGoogleId
  }

  def isValidTokenId(tokenId: String): (GoogleIdToken,Boolean) = {
    val googleInfo: GoogleIdToken = verifier.verify(tokenId)
    (googleInfo,googleInfo!=null)
  }
}
