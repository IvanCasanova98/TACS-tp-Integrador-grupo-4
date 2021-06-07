package unitTests.daoTests

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import exceptions.Exceptions.DeckNotFoundException
import models.DeckDbDTO
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import repositories.daos._

class DeckSQLDaoTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {
  val deckDaoTest: DeckSQLDao = new DeckSQLDao(db = H2DB())

  "Deck SQL Dao" when {
    "Creating Deck" should {
      "create deck ok" in {
        val deckId: Int = deckDaoTest.createDeck("holi", List(1, 2, 3))

        deckDaoTest.getDeckById(1) shouldBe DeckDbDTO(1, "holi", List(1, 2, 3))
        deckId shouldBe 1
      }

    }
    "Updating Deck" should {
      "update deck ok" in {
        val deckId: Int = deckDaoTest.createDeck("holi", List(1, 2, 3))
        deckDaoTest.updateDeck(DeckDbDTO(deckId, "nuevo-holi", List(1, 2, 3)))

        deckDaoTest.getDeckById(deckId) shouldBe DeckDbDTO(1, "nuevo-holi", List(1, 2, 3))
      }

      "update deck fail" in {
        the[DeckNotFoundException] thrownBy deckDaoTest.getDeckById(1111)

      }
    }
    "Deleting Deck" should {
      "delete deck ok" in {
        val deckId: Int = deckDaoTest.createDeck("holi", List(1, 2, 3))
        deckDaoTest.deleteDeck(deckId)
        the[DeckNotFoundException] thrownBy deckDaoTest.getDeckById(1)
      }
    }
  }
}
