package unitTests.daoTests

import db.H2DB
import models.MatchStatus.{CREATED, IN_PROCESS}
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.scalatest.{Matchers, WordSpec}
import repositories.daos.{DeckSQLDao, MatchSQLDao}
import repositories.{DeckRepository, MatchRepository}

import java.sql.Connection

class MatchSQLDaoTest extends WordSpec with Matchers {
  val sqlDB: Connection = H2DB()
  val deckRepository = new DeckRepository(new DeckSQLDao(sqlDB))
  val matchRepository = new MatchRepository(new MatchSQLDao(sqlDB))

  "MatchSQLDao test" when {
    "creating match" should {
      "Create a match with specified values" in {
        val deckId = deckRepository.createDeck("deck", List(1, 4, 5, 3, 2))
        val matchId = matchRepository.createMatch(deckId, "userId1", "userId2")

        val insertedMatch = matchRepository.getMatchById(matchId)
        insertedMatch.matchCreatorId shouldBe "userId1"
        insertedMatch.matchId shouldBe matchId
        insertedMatch.status shouldBe CREATED
      }
      "Fail because of foreign key if deck doesn't exist in database" in {
        the[JdbcSQLIntegrityConstraintViolationException] thrownBy matchRepository.createMatch(34234, "userId1", "userId2")
      }
    }
    "Updating match winner" should {
      "Update a winner with user id" in {
        val deckId = deckRepository.createDeck("someDeck", List(33, 4, 1, 5, 6, 7))
        val matchId = matchRepository.createMatch(deckId, "creatorId", "challengedId")
        matchRepository.updateMatchWinner(matchId, "creatorId")

        matchRepository.getMatchById(matchId).winnerId shouldBe Some("creatorId")
      }
      "Update winner with tie result" in {
        val deckId = deckRepository.createDeck("someDeck", List(33, 4, 1, 5, 6, 7))
        val matchId = matchRepository.createMatch(deckId, "creatorId", "challengedId")
        matchRepository.updateMatchWinner(matchId, "TIE")

        matchRepository.getMatchById(matchId).winnerId shouldBe Some("TIE")
      }
    }
    "Updating match status" should {
      "Update status and return object match status" in {
        val deckId = deckRepository.createDeck("someDeck", List(33, 4, 1, 5, 6, 7))
        val matchId = matchRepository.createMatch(deckId, "creatorId", "challengedId")
        matchRepository.updateMatchStatus(matchId, "IN_PROCESS")

        matchRepository.getMatchById(matchId).status shouldBe IN_PROCESS
      }
    }
  }
}
