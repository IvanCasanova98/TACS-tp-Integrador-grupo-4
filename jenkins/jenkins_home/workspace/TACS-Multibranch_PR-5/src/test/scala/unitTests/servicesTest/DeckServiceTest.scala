package unitTests.servicesTest

import models.{Card, Deck, DeckDbDTO}
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import repositories.DeckRepository
import repositories.daos.DeckLocalDao
import routes.inputs.DeckInputs.PartialDeckInput
import services.{DeckService, SuperheroApi}

import scala.collection.mutable

class DeckServiceTest extends AnyWordSpecLike with Matchers {
  val db: mutable.HashMap[Int, DeckDbDTO] = mutable.HashMap()
  val superheroApiMock: SuperheroApi = mock[SuperheroApi]
  val deckService = new DeckService(new DeckRepository(new DeckLocalDao(db)), superheroApiMock)

  "Deck service" when {
    "Getting deck by id" should {
      "Return deck from db" in {
        val deckId = deckService.createDeck(PartialDeckInput("my_deck", List(2, 5, 3, 1, 9)))
        val deckDbDTO = deckService.getDeckById(deckId)
        deckDbDTO shouldBe DeckDbDTO(deckId, "my_deck", List(2, 5, 3, 1, 9))
        db.clear()
      }
      "Return complete deck with cards" in {
        val deckId = deckService.createDeck(PartialDeckInput("my_deck", List(2, 5, 3, 1, 9)))
        when(superheroApiMock.getHeroById(anyInt())).thenReturn(Card(1, "card", List(), "url"))
        val completeDeck = deckService.getCompleteDeckById(deckId)
        completeDeck shouldBe Deck(deckId, "my_deck",
          List(Card(1, "card", List(), "url"), Card(1, "card", List(), "url"), Card(1, "card", List(), "url"), Card(1, "card", List(), "url"), Card(1, "card", List(), "url")))

        db.clear()
      }
    }
    "Get all decks" should {
      "return every deck in db" in {
        val deckId1 = deckService.createDeck(PartialDeckInput("my_deck1", List(2, 5, 3, 1, 9)))
        val deckId2 = deckService.createDeck(PartialDeckInput("my_deck2", List(2, 5, 3, 1, 9)))

        val decks = deckService.getAll
        decks.size shouldBe 2
        decks.find(_.name == "my_deck1").get.id shouldBe deckId1
        decks.find(_.name == "my_deck2").get.id shouldBe deckId2
        db.clear()
      }
    }
  }
}
