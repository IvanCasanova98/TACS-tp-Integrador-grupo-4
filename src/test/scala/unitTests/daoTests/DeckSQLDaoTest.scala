package unitTests.daoTests

import org.junit.{Before, Test}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import models.{Deck, DeckDbDTO}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import repositories.daos._
import serializers.Json4sSnakeCaseSupport

class DeckSQLDaoTest  extends WordSpec with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport with BeforeAndAfter{
  val deckDaoTest: DeckSQLDao = new DeckSQLDao( db = H2DB())

  "Deck SQL Dao" when {
    "Creating Deck" should {
      "create deck ok" in {
        deckDaoTest.createDeck("holi",List(1,2,3))

        deckDaoTest.getDeckById(1) shouldBe DeckDbDTO(1,"holi",List(1,2,3))
      }
    }
  }
}
