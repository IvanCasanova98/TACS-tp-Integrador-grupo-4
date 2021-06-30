package unitTests.servicesTest

import com.google.api.client.googleapis.auth.oauth2.{GoogleIdToken, GoogleIdTokenVerifier}
import com.google.api.client.json.webtoken.JsonWebSignature.Header
import models.PlayerPermissions
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import repositories.PlayerRepository
import routes.inputs.LoginInputs.LoginInput
import services.LoginService

class LoginServiceTest extends AnyWordSpecLike with Matchers{
  val playerRepositoryMock: PlayerRepository = mock[PlayerRepository]
  val googleVerifier: GoogleIdTokenVerifier = mock[GoogleIdTokenVerifier]
  val loginService: LoginService = new LoginService(playerRepositoryMock, googleVerifier)

  "Login service" when {
    "Is valid token id" should {
      "Return false when token is invalid" in {
        when(googleVerifier.verify("easd23423da")).thenReturn(null)
        loginService.isValidTokenId("easd23423da") shouldBe (null, false)
      }
    }
    "Validate player with google" should {
      "Return true if player is valid" in {
        when(googleVerifier.verify("easd2342a")).thenReturn(new GoogleIdToken(new Header(), new GoogleIdToken.Payload().setSubject("id"), Array.emptyByteArray, Array.emptyByteArray))
        loginService.validatePlayerWithGoogle("id", "easd2342a") shouldBe true
      }
      "Return false if player is not valid" in {
        when(googleVerifier.verify("easd2342a")).thenReturn(new GoogleIdToken(new Header(), new GoogleIdToken.Payload().setSubject("id2"), Array.emptyByteArray, Array.emptyByteArray))
        loginService.validatePlayerWithGoogle("id", "easd2342a") shouldBe false
      }
    }
    "Get player permissions" should {
      "Return jwt" in {
        val loginInput = LoginInput("Luke Skywalker", "luke@galaxy.com", "image", "1uk3", "easd2342a")
        when(googleVerifier.verify("easd2342a")).thenReturn(new GoogleIdToken(new Header(), new GoogleIdToken.Payload().setSubject("1uk3"), Array.emptyByteArray, Array.emptyByteArray))
        when(playerRepositoryMock.getOrCreatePlayerPermissions(loginInput)).thenReturn(PlayerPermissions(true, true, true))
        val response = loginService.getPlayerPermissions(loginInput)
        response("access_token").contains("ey") shouldBe true
      }
      "Return jwt with unauthorized information encoded" in {
        val loginInput = LoginInput("Luke Skywalker", "luke@galaxy.com", "image", "1uk3", "easd2342a")
        when(googleVerifier.verify("easd2342a")).thenReturn(null)

        val response = loginService.getPlayerPermissions(loginInput)
        response("access_token").contains("ey") shouldBe true
      }
    }
  }
}
