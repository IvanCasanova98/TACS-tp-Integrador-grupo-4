package integrationTests

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import models.Player
import org.mockito.MockitoSugar.mock
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.{Matchers, WordSpec}
import repositories.{DeckRepository, MatchRepository, MovementRepository, PlayerRepository}
import repositories.daos.{MatchLocalDAO, MatchSQLDao}
import repositories.dbdtos.MatchDBDTO
import routes.MatchRoutes
import routes.inputs.MatchInputs.{PostMatchDTO, UpdateMatchStatus}
import serializers.Json4sSnakeCaseSupport
import services.{ConnectedPlayersService, DeckService, MatchService}

import java.sql.Connection
import scala.collection.mutable

class MatchServiceIntegrationTest  extends WordSpec with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport {
  val db:mutable.HashMap[Int, MatchDBDTO] = mutable.HashMap()
  val playerDb: mutable.HashMap[Player, Player] = mutable.HashMap()
  val sqlSB: Connection = H2DB()
  val matchService = new MatchService(new MatchRepository(new MatchSQLDao(sqlSB)), mock[PlayerRepository], mock[DeckService], mock[MovementRepository])
  val matchRoutes: Route = MatchRoutes(matchService, mock[ConnectedPlayersService])

  def postMatchEntity(postMatchDTO: PostMatchDTO): MessageEntity = Marshal(postMatchDTO).to[MessageEntity].futureValue

  def patchMatchStatus(patchMatchStatus: UpdateMatchStatus): MessageEntity = Marshal(patchMatchStatus).to[MessageEntity].futureValue

  "Match service" should {
    "Return 201 and id whe posting new match" in {
      Post("/matches").withEntity(postMatchEntity(PostMatchDTO(1, "userId", "anotherUserId"))) ~> matchRoutes ~>
        check {
          response.status shouldBe StatusCodes.Created
          db.contains(1) shouldBe true
        }
    }
    "Return match of user" in {
      Post("/matches").withEntity(postMatchEntity(PostMatchDTO(1, "userId", "anotherUserId"))) ~> matchRoutes
      Patch("/matches/1/status").withEntity(patchMatchStatus(UpdateMatchStatus("IN_PROCESS")))~> matchRoutes ~>
        check {
          response.status shouldBe StatusCodes.NoContent
          db(1).status.name() shouldBe "IN_PROCESS"
        }
    }
  }

}
