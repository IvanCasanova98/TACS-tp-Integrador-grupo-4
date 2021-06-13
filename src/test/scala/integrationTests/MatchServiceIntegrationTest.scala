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
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import repositories.daos.{DeckSQLDao, MatchSQLDao, PlayerSQLDao}
import repositories.{MatchRepository, MovementRepository, PlayerRepository}
import routes.MatchRoutes
import routes.inputs.MatchInputs.{PostMatchDTO, UpdateMatchStatus}
import serializers.Json4sSnakeCaseSupport
import services.{ConnectedPlayersService, DeckService, MatchService}

import java.sql.Connection

class MatchServiceIntegrationTest extends WordSpec with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport with BeforeAndAfter {
  val sqlDB: Connection = H2DB()
  val matchRepo = new MatchRepository(new MatchSQLDao(sqlDB))
  val matchService = new MatchService(matchRepo, mock[PlayerRepository], mock[DeckService], mock[MovementRepository])
  val connectedPlayersService: ConnectedPlayersService = mock[ConnectedPlayersService]
  val matchRoutes: Route = MatchRoutes(matchService, connectedPlayersService)
  val playerSQLDao = new PlayerSQLDao(sqlDB)
  val deckSQLDao = new DeckSQLDao(sqlDB)

  def postMatchEntity(postMatchDTO: PostMatchDTO): MessageEntity = Marshal(postMatchDTO).to[MessageEntity].futureValue

  def patchMatchStatus(patchMatchStatus: UpdateMatchStatus): MessageEntity = Marshal(patchMatchStatus).to[MessageEntity].futureValue

  before {
    H2DB.resetTables(sqlDB)
    playerSQLDao.createPlayer(Player("userId", "", "", false, false))
    playerSQLDao.createPlayer(Player("anotherUserId", "", "", false, false))
  }

  "Match service" should {

    "Return 201 and id when posting new match" in {
      val deckId = deckSQLDao.createDeck("deck", List(3, 2, 5))
      val postMatchDTO = PostMatchDTO(deckId, "userId", "anotherUserId")
      Post("/matches").withEntity(postMatchEntity(postMatchDTO.copy(deckId = deckId))) ~> matchRoutes ~>
        check {
          response.status shouldBe StatusCodes.Created
        }
    }
    "Return match of user" in {
      val deckId = deckSQLDao.createDeck("deck", List(3, 2, 5))

      Post("/matches").withEntity(postMatchEntity(PostMatchDTO(deckId, "userId", "anotherUserId"))) ~> matchRoutes ~> check {
        val id = responseAs[Int]
        Patch(s"/matches/$id/status").withEntity(patchMatchStatus(UpdateMatchStatus("IN_PROCESS"))) ~> matchRoutes ~>
          check {
            response.status shouldBe StatusCodes.NoContent
          }
      }
    }
  }

}
