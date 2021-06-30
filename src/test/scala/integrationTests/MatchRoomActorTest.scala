package integrationTests

import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, PropertyNamingStrategy, SerializationFeature}
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import models.AttributeName.{AttributeName, HEIGHT, INTELLIGENCE, STRENGTH}
import models.{Attribute, AttributeNameDeserializer, AttributeNameSerializer, Card, Deck, Match, Player}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{doNothing, when}
import org.mockito.MockitoSugar.mock
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import routes.PlayRoutes
import serializers.JsonParser
import services.{ConnectedPlayersService, MatchRooms, MatchService}

class MatchRoomActorTest extends AnyWordSpecLike with ScalatestRouteTest with Matchers with BeforeAndAfter {
  def defaultObjectMapper(): ObjectMapper = {
    val customModule = new SimpleModule("CustomModule")
      .addSerializer(classOf[AttributeName], new AttributeNameSerializer(classOf[AttributeName]))
      .addDeserializer(classOf[AttributeName], new AttributeNameDeserializer(classOf[AttributeName]))

    new ObjectMapper()
      .registerModule(DefaultScalaModule)
      .registerModule(new Jdk8Module)
      .registerModule(new JavaTimeModule)
      .registerModule(customModule)
      .disable(SerializationFeature.INDENT_OUTPUT)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
      .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
  }
  val matchServiceMock: MatchService = mock[MatchService]
  val jsonParser = new JsonParser(defaultObjectMapper())
  var matchRooms = new MatchRooms(system, matchServiceMock, jsonParser)
  val aBombCard: Card = Card(1, "A-bomb", List(Attribute(STRENGTH, 300), Attribute(HEIGHT, 200)), "")
  val deck: Deck = Deck(7, "deck",
    List(aBombCard, aBombCard.copy(id = 399), aBombCard.copy(id = 3423), aBombCard.copy(id = 45363),
      aBombCard.copy(id = 3, name = "saraza", powerStats = List(Attribute(STRENGTH, 800))),
      aBombCard.copy(id = 40, name = "monster", powerStats = List(Attribute(INTELLIGENCE, 300))),
      aBombCard.copy(id = 5, name = "Ajax", powerStats = List(Attribute(INTELLIGENCE, 234)))))
  val matchInfo: Match = Match(1, "CREATED", Player("526151", "user1", "", false, false),
    Player("12345", "user2", "", false, false), deck, List(), None)

  before {
    matchRooms = new MatchRooms(system, matchServiceMock, jsonParser)
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
    "Play turn" in {
      val routes = PlayRoutes(mock[ConnectedPlayersService], matchRooms)
      val wsClient1: WSProbe = WSProbe()
      val wsClient2: WSProbe = WSProbe()

      when(matchServiceMock.isUserAuthorizedToJoinMatch(1, "526151")).thenReturn(true)
      when(matchServiceMock.isUserAuthorizedToJoinMatch(1, "12345")).thenReturn(true)
      when(matchServiceMock.findMatchById(1)).thenReturn(matchInfo)
      when(matchServiceMock.getDeckCountOfMatch(any())).thenReturn(4)
      when(matchServiceMock.getMovementResult(any(), any())).thenReturn("12345")
      doNothing().when(matchServiceMock).updateMatchStatus(1, "IN_PROCESS")

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
          wsClient1.sendMessage("CONNECT GAME")
          wsClient1.expectMessage("{\"event\":\"INIT\",\"deck_count\":4,\"opponent\":{\"user_id\":\"12345\",\"user_name\":\"user2\",\"image_url\":\"\",\"score\":0},\"creator\":{\"user_id\":\"526151\",\"user_name\":\"user1\",\"image_url\":\"\",\"score\":0}}")
          wsClient1.expectMessage().toString.contains("TURN")
          wsClient2.sendMessage("CONNECT GAME")
          wsClient1.sendMessage("SET_ATTRIBUTE:STRENGTH")
          val movementResult = wsClient1.expectMessage().toString
          assert(movementResult.contains("MOVEMENT_RESULT"))
          assert(movementResult.contains("\"winner_id\":\"12345\""))
          wsClient1.sendMessage("ABANDON")
          wsClient2.expectMessage().toString.contains("MATCH_RESULT")
        }
    }
  }

}
