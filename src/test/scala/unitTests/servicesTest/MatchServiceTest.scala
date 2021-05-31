package unitTests.servicesTest

import models.AttributeName.{HEIGHT, INTELLIGENCE, POWER, STRENGTH}
import models.{Attribute, Card}
import org.mockito.MockitoSugar.mock
import org.scalatest.{Matchers, WordSpec}
import repositories.daos.MatchLocalDAO
import repositories.dbdtos.MatchDBDTO
import repositories.{MatchRepository, MovementRepository, PlayerRepository}
import services.{DeckService, MatchService}

import scala.collection.immutable.HashMap
import scala.collection.mutable

class MatchServiceTest extends WordSpec with Matchers {
  val db:mutable.HashMap[Int, MatchDBDTO] = mutable.HashMap()
  val matchService = new MatchService(new MatchRepository(new MatchLocalDAO(db)), mock[PlayerRepository], mock[DeckService], mock[MovementRepository])

  "Match service test" when {
    "Calculate winner id of movement" should {
      "return player2 as winner" in {
        val cardsMap = mutable.HashMap("player1" -> Card(1, "A-bomb", List(Attribute(STRENGTH, 300), Attribute(HEIGHT, 200)), ""),
        "player2" -> Card(2, "Somecard", List(Attribute(STRENGTH, 500), Attribute(HEIGHT, 100)), ""))

        val result = matchService.getMovementResult(cardsMap, STRENGTH)

        result shouldBe "player2"
      }
      "return player1 as winner" in {
        val cardsMap = mutable.HashMap("player1" -> Card(1, "A-bomb", List(Attribute(INTELLIGENCE, 125), Attribute(POWER, 200)), ""),
          "player2" -> Card(2, "Somecard", List(Attribute(INTELLIGENCE, 50), Attribute(POWER, 100)), ""))

        val result = matchService.getMovementResult(cardsMap, INTELLIGENCE)

        result shouldBe "player1"
      }
      "return TIE if values are the same for chosen attribute" in {
        val cardsMap = mutable.HashMap("player1" -> Card(1, "A-bomb", List(Attribute(INTELLIGENCE, 125), Attribute(POWER, 200)), ""),
          "player2" -> Card(2, "Somecard", List(Attribute(INTELLIGENCE, 125), Attribute(POWER, 100)), ""))

        val result = matchService.getMovementResult(cardsMap, INTELLIGENCE)

        result shouldBe "TIE"
      }
    }
  }

}
