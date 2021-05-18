package unitTests
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import org.scalatest.{Matchers, WordSpec}
import routes.Routes

class TestRoutes extends WordSpec with Matchers with ScalatestRouteTest {
  "The routes" should {

    "websocket connection to home test" in {
      val wsClient = WSProbe()

      WS("/home?userId=52615", wsClient.flow) ~> Routes() ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient.expectMessage("[\"{\\\"user_name\\\":\\\"NOT-FOUND\\\",\\\"user_id\\\":\\\"\\\"}\"]")
        }
    }
  }
}
