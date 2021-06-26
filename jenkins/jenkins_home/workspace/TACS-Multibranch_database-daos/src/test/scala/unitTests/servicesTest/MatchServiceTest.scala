package unitTests.servicesTest

import models.AttributeName.{HEIGHT, INTELLIGENCE, POWER, STRENGTH}
import models.{Attribute, Card, Deck, Match, Movement, Player}
import org.mockito.MockitoSugar.mock
import org.scalatest.{Matchers, WordSpec}
import repositories.daos.MatchLocalDAO
import repositories.dbdtos.MatchDBDTO
import repositories.{MatchRepository, MovementRepository, PlayerRepository}
import services.{DeckService, MatchService}
import scala.collection.mutable

class MatchServiceTest extends WordSpec with Matchers {
  val db: mutable.HashMap[Int, MatchDBDTO] = mutable.HashMap()
  val matchService = new MatchService(new MatchRepository(new MatchLocalDAO(db)), mock[PlayerRepository], mock[DeckService], mock[MovementRepository])
  val aBombCard: Card = Card(1, "A-bomb", List(Attribute(STRENGTH, 300), Attribute(HEIGHT, 200)), "")
  val deck: Deck = Deck(7, "deck",
    List(aBombCard, aBombCard.copy(id=399), aBombCard.copy(id=3423), aBombCard.copy(id=45363),
      aBombCard.copy(id = 3, name = "saraza", powerStats = List(Attribute(STRENGTH, 800))),
      aBombCard.copy(id = 40, name = "monster", powerStats = List(Attribute(INTELLIGENCE, 300))),
      aBombCard.copy(id = 5, name = "Ajax", powerStats = List(Attribute(INTELLIGENCE, 234)))))
  val movements = List(Movement(1, STRENGTH.name(), 1, 3, "1222", "1222"),
    Movement(2, INTELLIGENCE.name(), 5, 40, "1222", "1333"))
  val matchInfo: Match = Match(1, "PAUSED", Player("1333", "player1", "", false, false), Player("1222", "player2", "", false, false), deck, movements, None)

  "Match service test" when {
    "Calculate winner id of movement" should {
      "return player2 as winner" in {
        val cardsMap = mutable.HashMap("player1" -> aBombCard,
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
    "Reload match stats if it was paused" should {
      "Get players score" in {
        matchService.countWonMovements(movements, matchInfo.matchCreator.userId) shouldBe 0
        matchService.countWonMovements(movements, matchInfo.challengedPlayer.userId) shouldBe 2
      }
    }
    "return deck count minus moves made" in {
      matchService.getDeckCountOfMatch(matchInfo) shouldBe 1
    }
  }

}
