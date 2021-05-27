package services

import models.{Deck, DeckDbDTO, Match}
import routes.inputs.DeckInputs.PartialDeckInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import repositories.DeckRepository

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DeckService(deckRepository: DeckRepository, superheroApi: SuperheroApi) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  /**
   * Returns complete deck with cards fetched from superhero api in parallel
   * @param deckId
   * @return deck
   */
  def getCompleteDeckById(deckId: Int): Deck = {
    val deck = deckRepository.getDeckById(deckId)
    val fetchCardsInParallel = Future.sequence(deck.cardIds.map(id => Future {
      superheroApi.get_hero_by_id(id)
    }))
    val cards = Await.result(fetchCardsInParallel, Duration.apply(20, TimeUnit.SECONDS))
    Deck(deckId, deck.name, cards)
  }

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])


  def getAll: List[DeckDbDTO] = {
    logger.info(s"Listing all decks")
    deckRepository.getDecks
  }

  def createDeck(deck: PartialDeckInput): Int = {
    logger.info(s"Creating new deck with name: ${deck.name}")
    deckRepository.createDeck(deck.name, deck.cardIds)
  }

  def updateDeck(deckId: Int, deckInput: PartialDeckInput): Unit = {
    logger.info(s"Updating deck $deckId with: $deckInput")
    val deck = DeckDbDTO(deckId, deckInput.name, deckInput.cardIds)
    deckRepository.updateDeck(deck)
  }

  def deleteDeck(deckId: Int): DeckDbDTO = {
    logger.info(s"Deleting deck $deckId")
    deckRepository.deleteDeck(deckId)
  }

}
