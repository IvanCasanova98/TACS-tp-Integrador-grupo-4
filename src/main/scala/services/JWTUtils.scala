package services
import io.really.jwt._
import play.api.libs.json.Json
import java.time.Instant

case class JWTUtils() {
  def getJWT(googleId: String, tokenId: String, isAuthenticated: Boolean, isAuthorized: Boolean, isAdmin: Boolean): String = {
    val unixTimestamp : Long = Instant.now.getEpochSecond + 86400
    val payload = Json.obj("googleId" -> googleId, "isAuthenticated" -> isAuthenticated,"isAuthorized"->isAuthorized, "isAdmin"-> isAdmin, "exp"-> unixTimestamp, "iat"-> Instant.now.getEpochSecond)
    JWT.encode("secret-key", payload)
  }
}
