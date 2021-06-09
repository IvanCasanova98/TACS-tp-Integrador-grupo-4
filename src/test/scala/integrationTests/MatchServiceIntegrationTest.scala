package integrationTests

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import models.Player
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doNothing
import org.mockito.MockitoSugar.mock
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.{Matchers, WordSpec}
import repositories.daos.{DeckSQLDao, MatchSQLDao, PlayerSQLDao}
import repositories.{MatchRepository, MovementRepository, PlayerRepository}
import routes.MatchRoutes
import routes.inputs.MatchInputs.{PostMatchDTO, UpdateMatchStatus}
import serializers.Json4sSnakeCaseSupport
import services.{ConnectedPlayersService, DeckService, MatchService}

import java.sql.Connection

class MatchServiceIntegrationTest  extends WordSpec with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport {
  val sqlDB: Connection = H2DB()
  val matchRepo = new MatchRepository(new MatchSQLDao(sqlDB))
  val matchService = new MatchService(matchRepo, mock[PlayerRepository], mock[DeckService], mock[MovementRepository])
  val connectedPlayersService: ConnectedPlayersService =  mock[ConnectedPlayersService]
  val matchRoutes: Route = MatchRoutes(matchService,connectedPlayersService)
  val playerSQLDao = new PlayerSQLDao(sqlDB)

  def postMatchEntity(postMatchDTO: PostMatchDTO): MessageEntity = Marshal(postMatchDTO).to[MessageEntity].futureValue

  def patchMatchStatus(patchMatchStatus: UpdateMatchStatus): MessageEntity = Marshal(patchMatchStatus).to[MessageEntity].futureValue

  "Match service" should {
    playerSQLDao.createPlayer(Player("userId", "", "", false, false))
    playerSQLDao.createPlayer(Player("anotherUserId", "", "", false, false))
    val deckId = new DeckSQLDao(sqlDB).createDeck("deck", List(3,2,5))

    "Return 201 and id whe posting new match" in {
      val postMatchDTO = PostMatchDTO(deckId, "userId", "anotherUserId")
      doNothing().when(connectedPlayersService).sendMessageToUserId(anyString(), anyString())
      matchService.createMatch(postMatchDTO.deckId, postMatchDTO.matchCreatorId, postMatchDTO.challengedPlayerId)
      Post("/matches").withEntity(postMatchEntity(postMatchDTO)) ~> matchRoutes ~>
        check {
          response.status shouldBe StatusCodes.Created
        }
    }
    "Return match of user" in {
      Post("/matches").withEntity(postMatchEntity(PostMatchDTO(1, "userId", "anotherUserId"))) ~> matchRoutes ~> check {
        val id = responseAs[Int]
        Patch(s"/matches/$id/status").withEntity(patchMatchStatus(UpdateMatchStatus("IN_PROCESS"))) ~> matchRoutes ~>
          check {
            response.status shouldBe StatusCodes.NoContent
          }
      }
    }
  }

}
