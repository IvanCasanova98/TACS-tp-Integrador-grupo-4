package unitTests
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import org.scalatest.{Matchers, WordSpec}
import routes.Routes

class WebsocketRoutes extends WordSpec with Matchers with ScalatestRouteTest {
  "Play routes" when {

    "Websocket connection to home of not logged in user" in {
      val wsClient = WSProbe()

      WS("/home?userId=52615", wsClient.flow) ~> Routes() ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient.expectMessage("[\"{\\\"user_name\\\":\\\"NOT-FOUND\\\",\\\"user_id\\\":\\\"52615\\\"}\"]")
        }
    }
  }
}
