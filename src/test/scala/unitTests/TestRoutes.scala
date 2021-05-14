package unitTests
import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.util.ByteString
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.{Matchers, WordSpec}
import routes.Routes

class TestRoutes extends WordSpec with Matchers with ScalatestRouteTest {
  "The routes" should {

    "websocket connection test" in {
      // tests:
      // create a testing probe representing the client-side
      val wsClient = WSProbe()

      // WS creates a WebSocket request for testing
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
