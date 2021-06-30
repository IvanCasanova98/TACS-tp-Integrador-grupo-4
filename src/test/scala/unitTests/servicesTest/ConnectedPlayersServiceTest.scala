package unitTests.servicesTest

import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import models.Player
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import repositories.PlayerRepository
import routes.PlayRoutes
import services.{ConnectedPlayersService, MatchRooms}

class ConnectedPlayersServiceTest extends AnyWordSpecLike with ScalatestRouteTest with Matchers with BeforeAndAfter{

  val playerRepositoryMock: PlayerRepository = mock[PlayerRepository]
  val connectedPlayersService = new ConnectedPlayersService(system, playerRepositoryMock)
  "Connected players service" when {
    "Player joins server" in {
      val routes = PlayRoutes(connectedPlayersService, mock[MatchRooms])
      val wsClient: WSProbe = WSProbe()

      WS("/home?userId=52615", wsClient.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldBe true
        }
    }
    "Player joins server and gets connected players list" in {
      val routes = PlayRoutes(connectedPlayersService, mock[MatchRooms])
      val wsClient1: WSProbe = WSProbe()
      val wsClient2: WSProbe = WSProbe()
      when(playerRepositoryMock.getPlayerById("52615")).thenReturn(Player("52615", "John", "imageurl", false, false))

      WS("/home?userId=52615", wsClient1.flow) ~> routes ~>
        check {
          wsClient1.expectMessage("[\"{\\\"user_name\\\":\\\"John\\\",\\\"user_id\\\":\\\"52615\\\"}\"]")
        }
    }
  }
}
