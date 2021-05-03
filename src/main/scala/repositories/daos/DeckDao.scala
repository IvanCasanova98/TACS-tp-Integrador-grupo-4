package repositories.daos

import models.Deck

trait DeckDao {

  def createDeck(deckName: String, cardIds: List[Int]): Int
  def updateDeck(deck: Deck): Unit
  def deleteDeck(deckId: Int): Deck
  def getDecks: List[Deck]
}



