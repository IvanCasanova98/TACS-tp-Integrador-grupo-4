package unitTests.daoTests

import db.H2DB
import models.MatchStatus.{CREATED, IN_PROCESS}
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpec}
import repositories.daos.{DeckSQLDao, MatchSQLDao, PlayerSQLDao}
import repositories.{DeckRepository, MatchRepository, PlayerRepository}
import routes.inputs.LoginInputs.LoginInput

import java.sql.Connection

class MatchSQLDaoTest extends WordSpec with Matchers with BeforeAndAfter {
  val sqlDB: Connection = H2DB()
  val deckRepository = new DeckRepository(new DeckSQLDao(sqlDB))
  val matchRepository = new MatchRepository(new MatchSQLDao(sqlDB))
  val playerRepository = new PlayerRepository(new PlayerSQLDao(sqlDB))
  val basicPlayer: LoginInput = LoginInput("user", "", "", "creatorId", "")
  var deckId: Int = -1

  before {
    sqlDB.prepareStatement("DELETE FROM movements").execute()
    sqlDB.prepareStatement("DELETE FROM matches").execute()
    sqlDB.prepareStatement("DELETE FROM decks").execute()
    sqlDB.prepareStatement("DELETE FROM players").execute()

    playerRepository.getOrCreatePlayerPermissions(basicPlayer)
    playerRepository.getOrCreatePlayerPermissions(basicPlayer.copy(googleId = "challengedId"))
    deckId = deckRepository.createDeck("someDeck", List(33, 4, 1, 5, 6, 7))
  }

  "MatchSQLDao test" when {

    "creating match" should {
      "Create a match with specified values" in {
        val matchId = matchRepository.createMatch(deckId, "creatorId", "challengedId")

        val insertedMatch = matchRepository.getMatchById(matchId)
        insertedMatch.matchCreatorId shouldBe "creatorId"
        insertedMatch.matchId shouldBe matchId
        insertedMatch.status shouldBe CREATED
      }
      "Fail because of foreign key if deck doesn't exist in database" in {
        the[JdbcSQLIntegrityConstraintViolationException] thrownBy matchRepository.createMatch(34234, "userId1", "userId2")
      }
    }
    "Updating match winner" should {
      "Update a winner with user id" in {
        val matchId = matchRepository.createMatch(deckId, "creatorId", "challengedId")
        matchRepository.updateMatchWinner(matchId, "creatorId")

        matchRepository.getMatchById(matchId).winnerId shouldBe Some("creatorId")
      }
      "Update winner with tie result" in {
        val matchId = matchRepository.createMatch(deckId, "creatorId", "challengedId")
        matchRepository.updateMatchWinner(matchId, "TIE")

        matchRepository.getMatchById(matchId).winnerId shouldBe Some("TIE")
      }
    }
    "Updating match status" should {
      "Update status and return object match status" in {
        val matchId = matchRepository.createMatch(deckId, "creatorId", "challengedId")
        matchRepository.updateMatchStatus(matchId, "IN_PROCESS")

        matchRepository.getMatchById(matchId).status shouldBe IN_PROCESS
      }
    }
    "Getting matches of user" should {
      "Return matches that user created or was challenged" in {
        val matchIdCreator = matchRepository.createMatch(deckId, "creatorId", "challengedId")
        val matchIdChallenged = matchRepository.createMatch(deckId, "challengedId", "creatorId")

        val matches = matchRepository.getMatchesOfUser("creatorId")
        matches.size shouldBe 2
        matches.exists(m => m.matchId == matchIdCreator)
        matches.exists(m => m.matchId == matchIdChallenged)
      }
    }
  }
}
