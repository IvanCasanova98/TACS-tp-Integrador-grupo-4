package repositories.daos

import models.DeckDbDTO

trait DeckDao {
  def getDeckById(deckId: Int): DeckDbDTO
  def createDeck(deckName: String, cardIds: List[Int]): Int
  def updateDeck(deck: DeckDbDTO): Unit
  def deleteDeck(deckId: Int): Unit
  def getDecks: List[DeckDbDTO]
}



