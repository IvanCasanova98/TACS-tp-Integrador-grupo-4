/*package unitTests
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import routes.Routes

class WebsocketRoutes extends AnyWordSpecLike with Matchers with ScalatestRouteTest {
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
*/