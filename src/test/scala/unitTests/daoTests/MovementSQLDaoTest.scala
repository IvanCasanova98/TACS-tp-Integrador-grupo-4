package unitTests.daoTests

import java.sql.{Connection, ResultSet}

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import repositories.daos._
import repositories.{DeckRepository, MatchRepository, PlayerRepository}
import routes.inputs.LoginInputs.LoginInput

class MovementSQLDaoTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {
  var db: Connection = H2DB()
  val MovementDaoTest: MovementSQLDao = new MovementSQLDao(db)
  val basicPlayer: LoginInput = LoginInput("user", "", "", "creatorId", "")
  val deckRepository = new DeckRepository(new DeckSQLDao(db))
  val matchRepository = new MatchRepository(new MatchSQLDao(db))
  val playerRepository = new PlayerRepository(new PlayerSQLDao(db))
  var matchId1: Int = -1
  var matchId2: Int = -1

  before {
    H2DB.resetTables(db)
    playerRepository.getOrCreatePlayerPermissions(basicPlayer)
    playerRepository.getOrCreatePlayerPermissions(basicPlayer.copy(googleId = "challengedId"))
    val deckId = deckRepository.createDeck("someDeck", List(33, 4, 1, 5, 6, 7))
    matchId1 = matchRepository.createMatch(deckId, "creatorId", "challengedId")
    matchId2 = matchRepository.createMatch(deckId, "creatorId", "challengedId")
  }

  "SQL Movement" when {

    "Getting movements" should {
      "return all movements by matchId" in {
        val query1 = db.prepareStatement("INSERT INTO movements(match_id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn) values (?, 'fuerza', 3, 4, 'win', '123')")
        query1.setInt(1, matchId1)
        query1.execute()

        val query2 = db.prepareStatement("INSERT INTO movements(match_id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn) values (?, 'peso', 3, 4, 'win', '123')")
        query2.setInt(1, matchId2)
        query2.execute()

        val query3 = db.prepareStatement("INSERT INTO movements(match_id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn) values (?, 'altura', 3, 4, 'win', '123')")
        query3.setInt(1, matchId1)
        query3.execute()

        val movements = MovementDaoTest.getMovementsOfMatch(matchId1)

        movements.size shouldBe 2
        movements.find(_.attributeName == "fuerza").size shouldBe 1
        movements.find(_.attributeName == "altura").size shouldBe 1
      }
    }

    "Save movements" should {
      "save one movement matchId" in {
        MovementDaoTest.saveMovement(matchId1, 3, 4,"123", "fuerza", "win")
        val query3 = db.prepareStatement("SELECT * FROM movements")
        val movements: ResultSet = query3.executeQuery()
        movements.first() shouldBe true
      }
    }
  }
}
