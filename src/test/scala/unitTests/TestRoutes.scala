package unitTests
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import org.scalatest.{Matchers, WordSpec}
import routes.Routes

class TestRoutes extends WordSpec with Matchers with ScalatestRouteTest {
  "The routes" should {

    "websocket connection test" in {
      val wsClient = WSProbe()

      WS("/", wsClient.flow) ~> Routes() ~>
        check {
          // check response for WS Upgrade headers
          isWebSocketUpgrade shouldEqual true

          // manually run a WS conversation
          wsClient.sendMessage("Peter")
          wsClient.expectMessage("ECHO: Peter")

          wsClient.sendCompletion()
          wsClient.expectCompletion()
        }
    }

    "return pong when ping is called" in {
      Get("/ping") ~> Routes.apply() ~> check {
        responseAs[String].substring(1,responseAs[String].length()-1) shouldEqual "pong"
      }
    }
  }
}
