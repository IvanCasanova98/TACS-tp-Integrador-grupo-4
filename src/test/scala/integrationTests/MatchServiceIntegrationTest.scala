/*package integrationTests

import java.sql.Connection

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import models.Player
import org.mockito.MockitoSugar.mock
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import repositories.daos.{DeckSQLDao, MatchSQLDao, PlayerSQLDao}
import repositories.{MatchRepository, MovementRepository, PlayerRepository}
import routes.MatchRoutes
import routes.inputs.MatchInputs.{PostMatchDTO, UpdateMatchStatus}
import serializers.Json4sSnakeCaseSupport
import services.{ConnectedPlayersService, DeckService, MatchService}

class MatchServiceIntegrationTest extends AnyWordSpecLike with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport with BeforeAndAfter {
  val sqlDB: Connection = H2DB()
  val matchRepo = new MatchRepository(new MatchSQLDao(sqlDB))
  val matchService = new MatchService(matchRepo, mock[PlayerRepository], mock[DeckService], mock[MovementRepository])
  val connectedPlayersService: ConnectedPlayersService = mock[ConnectedPlayersService]
  val matchRoutes: Route = MatchRoutes(matchService, connectedPlayersService)
  val playerSQLDao = new PlayerSQLDao(sqlDB)
  val deckSQLDao = new DeckSQLDao(sqlDB)
  val authorization: Authorization = Authorization(OAuth2BearerToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJnb29nbGVJZCI6IjExNTc0ODAyODM4NzA3OTU0ODc1NyIsImlzQXV0aGVudGljYXRlZCI6dHJ1ZSwiaXNBdXRob3JpemVkIjp0cnVlLCJpc0FkbWluIjp0cnVlLCJleHAiOjExNjI0ODM1Mjc5LCJpYXQiOjE2MjQ4MzUyODB9.77-977-9zb4i77-977-977-977-977-9eWDvv73bqu-_vVPvv70B77-9De-_vcuK77-977-977-9Vg02MA"))

  def postMatchEntity(postMatchDTO: PostMatchDTO): MessageEntity = Marshal(postMatchDTO).to[MessageEntity].futureValue

  def patchMatchStatus(patchMatchStatus: UpdateMatchStatus): MessageEntity = Marshal(patchMatchStatus).to[MessageEntity].futureValue

  before {
    H2DB.resetTables(sqlDB)
    playerSQLDao.createPlayer(Player("userId", "", "", false, false))
    playerSQLDao.createPlayer(Player("anotherUserId", "", "", false, false))
  }
  after{
    H2DB.resetTables(sqlDB)
  }

  "Match service" should {

    "Return 201 and id when posting new match" in {
      val deckId = deckSQLDao.createDeck("deck", List(3, 2, 5))
      val postMatchDTO = PostMatchDTO(deckId, "userId", "anotherUserId")
      Post("/matches").withEntity(postMatchEntity(postMatchDTO.copy(deckId = deckId))).addHeader(authorization) ~> matchRoutes ~>
        check {
          response.status shouldBe StatusCodes.Created
        }
    }
    "Return match of user" in {
      val deckId = deckSQLDao.createDeck("deck", List(3, 2, 5))

      Post("/matches").withEntity(postMatchEntity(PostMatchDTO(deckId, "userId", "anotherUserId"))).addHeader(authorization) ~> matchRoutes ~> check {
        val id = responseAs[Int]
        Patch(s"/matches/$id/status").withEntity(patchMatchStatus(UpdateMatchStatus("IN_PROCESS"))).addHeader(authorization) ~> matchRoutes ~>
          check {
            response.status shouldBe StatusCodes.NoContent
          }
      }
    }
  }

}
*/