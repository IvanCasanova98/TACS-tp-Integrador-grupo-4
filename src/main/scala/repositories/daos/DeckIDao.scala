package repositories.daos

import models.Deck

trait DeckIDao {

  def createDeck(deckName: String, cardIds: List[Int]): Int
  def updateDeck(deck: Deck): Unit
  def deleteDeck(deckId: Int): Deck

}



