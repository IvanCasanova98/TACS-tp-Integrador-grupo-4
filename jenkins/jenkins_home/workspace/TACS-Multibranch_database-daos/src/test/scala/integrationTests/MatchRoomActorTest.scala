package integrationTests

import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import models.MatchStatus.CREATED
import org.scalatest.{Matchers, WordSpec}
import repositories.dbdtos.MatchDBDTO
import routes.Routes

import java.util.Date

class MatchRoomActorTest extends WordSpec with Matchers with ScalatestRouteTest {


  "Match rooms actor" should {
    "Allow owner player to join match" in {
      val routes = Routes()
      val wsClient: WSProbe = WSProbe()
      Routes.matchLocalDb.put(1, MatchDBDTO(1, CREATED, "52615", "1234",1, None, new Date()))

      WS("/join-match/1?userId=52615", wsClient.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
        }
    }

    "Allow invite to join match" in {
      val routes = Routes()
      val wsClient: WSProbe = WSProbe()
      Routes.matchLocalDb.put(1, MatchDBDTO(1, CREATED, "52615", "1234",1, None, new Date()))

      WS("/join-match/1?userId=1234", wsClient.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
        }
    }
    "Return player not allowed when an uninvited player tries to join match" in {
      val routes = Routes()
      val wsClient: WSProbe = WSProbe()
      Routes.matchLocalDb.put(1, MatchDBDTO(1, CREATED, "52615", "1234",1, None, new Date()))

      WS("/join-match/1?userId=526115", wsClient.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient.expectMessage("User 526115 is not allowed to join match 1")
        }
    }
    "Return IN_LOBBY event when both players have joined match" in {
      val routes = Routes()
      val wsClient1: WSProbe = WSProbe()
      val wsClient2: WSProbe = WSProbe()
      Routes.matchLocalDb.put(1, MatchDBDTO(1, CREATED, "52615", "1234", 1, None, new Date()))

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
      val routes = Routes()
      val wsClient1: WSProbe = WSProbe()
      val wsClient2: WSProbe = WSProbe()
      Routes.matchLocalDb.put(1, MatchDBDTO(1, CREATED, "52615", "1234",1, None, new Date()))

      WS("/join-match/1?userId=52615", wsClient1.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient1.sendMessage("READY:52615")
        }

      WS("/join-match/1?userId=1234", wsClient2.flow) ~> routes ~>
        check {
          isWebSocketUpgrade shouldEqual true
          wsClient2.expectMessage("IN_LOBBY:52615:1234")
          wsClient1.expectMessage("IN_LOBBY:52615:1234")

          wsClient2.sendMessage("READY:1234")

          wsClient1.expectMessage("OPPONENT_READY")

          wsClient1.expectMessage("ALL_READY")
          wsClient2.expectMessage("ALL_READY")
        }

    }

  }

}
