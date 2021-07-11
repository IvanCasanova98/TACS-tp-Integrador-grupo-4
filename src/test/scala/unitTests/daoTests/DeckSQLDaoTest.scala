package unitTests.daoTests

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import exceptions.Exceptions.DeckNotFoundException
import models.DeckDbDTO
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfter
import repositories.daos._

import java.sql.Connection

class DeckSQLDaoTest extends AnyWordSpecLike with Matchers with ScalatestRouteTest with BeforeAndAfter {
  var db: Connection = H2DB()
  val deckDaoTest: DeckSQLDao = new DeckSQLDao(db)

  before {
    H2DB.resetTables(db)
  }

  "Deck SQL Dao" when {
    "Getting decks" should {
      "return all decks" in {
        deckDaoTest.createDeck("firstDeck", List(1, 2, 3))
        deckDaoTest.createDeck("secondDeck", List(4, 5, 6, 7, 8))

        val decksInDb = deckDaoTest.getDecks
        decksInDb.size shouldBe 2
        decksInDb.find(_.name == "firstDeck").get.cardIds.size shouldBe 3
        decksInDb.find(_.name == "secondDeck").get.cardIds.size shouldBe 5
      }
    }
    "Creating Deck" should {
      "create deck ok" in {
        val deckId: Int = deckDaoTest.createDeck("holi", List(1, 2, 3))

        deckDaoTest.getDeckById(deckId) shouldBe DeckDbDTO(deckId, "holi", List(1, 2, 3))
      }

    }
    "Updating Deck" should {
      "update deck ok" in {
        val deckId: Int = deckDaoTest.createDeck("holi", List(1, 2, 3))
        deckDaoTest.updateDeck(DeckDbDTO(deckId, "nuevo-holi", List(1, 2, 3)))

        deckDaoTest.getDeckById(deckId) shouldBe DeckDbDTO(deckId, "nuevo-holi", List(1, 2, 3))
      }

      "update deck fail" in {
        the[DeckNotFoundException] thrownBy deckDaoTest.getDeckById(1111)

      }
    }
    "Deleting Deck" should {
      "delete deck ok" in {
        val deckId: Int = deckDaoTest.createDeck("holi", List(1, 2, 3))
        deckDaoTest.deleteDeck(deckId)
      }
    }
  }
}
