package integrationTests

import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import routes.PlayRoutes
import services.{ConnectedPlayersService, MatchRooms, MatchService}

class MatchRoomActorTest extends AnyWordSpecLike with ScalatestRouteTest with Matchers with BeforeAndAfter {
  val matchServiceMock: MatchService = mock[MatchService]
  var matchRooms = new MatchRooms(system, matchServiceMock)

  before {
    matchRooms = new MatchRooms(system, matchServiceMock)
  }

  "Match rooms actor" should {
    "Allow owner player to join match" in {
      val routes = PlayRoutes(mock[ConnectedPlayersService], matchRooms)
      val wsClient: WSProbe = WSProbe()
      when(matchServiceMock.isUserAuthorizedToJoinMatch(1, "52615")).thenReturn(true)

      WS("/join-match/1?userId=52615", wsClient.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldBe true
        }
    }

    "Return player not allowed when an uninvited player tries to join match" in {
      val routes = PlayRoutes(mock[ConnectedPlayersService], matchRooms)
      val wsClient: WSProbe = WSProbe()

      when(matchServiceMock.isUserAuthorizedToJoinMatch(1, "526115")).thenReturn(false)

      WS("/join-match/1?userId=526115", wsClient.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient.expectMessage("User 526115 is not allowed to join match 1")
        }
    }
    "Return IN_LOBBY event when both players have joined match" in {
      val routes = PlayRoutes(mock[ConnectedPlayersService], matchRooms)
      val wsClient1: WSProbe = WSProbe()
      val wsClient2: WSProbe = WSProbe()

      when(matchServiceMock.isUserAuthorizedToJoinMatch(1, "52615")).thenReturn(true)
      when(matchServiceMock.isUserAuthorizedToJoinMatch(1, "1234")).thenReturn(true)

      WS("/join-match/1?userId=52615", wsClient1.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
        }

      WS("/join-match/1?userId=1234", wsClient2.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient2.expectMessage("IN_LOBBY:52615:1234")
          wsClient1.expectMessage("IN_LOBBY:52615:1234")
        }

    }

    "Return ALL_READY event when both players are ready to play" in {
      val routes = PlayRoutes(mock[ConnectedPlayersService], matchRooms)
      val wsClient1: WSProbe = WSProbe()
      val wsClient2: WSProbe = WSProbe()
      when(matchServiceMock.isUserAuthorizedToJoinMatch(1, "526151")).thenReturn(true)
      when(matchServiceMock.isUserAuthorizedToJoinMatch(1, "12345")).thenReturn(true)

      WS("/join-match/1?userId=526151", wsClient1.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient1.sendMessage("READY:526151")
        }

      WS("/join-match/1?userId=12345", wsClient2.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient2.expectMessage("IN_LOBBY:526151:12345")
          wsClient1.expectMessage("IN_LOBBY:526151:12345")

          wsClient2.sendMessage("READY:12345")

          wsClient1.expectMessage("OPPONENT_READY")
          wsClient1.expectMessage("ALL_READY")
          wsClient2.expectMessage("ALL_READY")
        }

    }

  }

}
