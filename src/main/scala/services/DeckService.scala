package services

import models.Deck
import routes.inputs.DeckInputs.PartialDeckInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import repositories.DeckRepository

class DeckService(deckRepository: DeckRepository) {

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def createDeck(deck: PartialDeckInput): Int = {
    logger.info(s"Creating new deck with name: ${deck.name}")
    deckRepository.createDeck(deck.name, deck.cardIds)
  }

  def updateDeck(deckId: Int, deckInput: PartialDeckInput): Unit = {
    logger.info(s"Updating deck $deckId with: $deckInput")
    val deck = Deck(deckId, deckInput.name, deckInput.cardIds)
    deckRepository.updateDeck(deck)
  }

  def deleteDeck(deckId: Int): Deck = {
    logger.info(s"Deleting deck $deckId")
    deckRepository.deleteDeck(deckId)
  }

}
