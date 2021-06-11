package unitTests.daoTests

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import exceptions.Exceptions.DeckNotFoundException
import models.DeckDbDTO
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import repositories.daos._
import java.sql.Connection

class MovementSQLDaoTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {
  var db: Connection = H2DB()
  val MovementDaoTest: MovementSQLDao = new MovementSQLDao(db)

  before {
    db.prepareStatement("DELETE FROM movements").execute()
  }

  "Get SQL Movement" when {
    "Getting movements" should {
      "return all movements by matchId" in {
        db.prepareStatement("INSERT INTO movements(match_id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn) values (1, 'fuerza', 3, 4, 'win', '123')").execute()
        db.prepareStatement("INSERT INTO movements(match_id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn) values (2, 'peso', 3, 4, 'win', '123')").execute()
        db.prepareStatement("INSERT INTO movements(match_id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn) values (1, 'altura', 3, 4, 'win', '123')").execute()
        val movements = MovementDaoTest.getMovementsOfMatch(1)

        movements.size shoulBe 2
        movements.find(_.attributeName == "fuerza").size souldBe 1
        movements.find(_.attributeName == "altura").size souldBe 1
      }
    }
  }
}
