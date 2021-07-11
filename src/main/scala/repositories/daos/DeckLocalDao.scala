package repositories.daos

import exceptions.Exceptions.DeckNotFoundException
import models.DeckDbDTO

import scala.collection.mutable


class DeckLocalDao(db: mutable.HashMap[Int, DeckDbDTO]) extends DeckDao {
  var deckId: Int = db.keys.size

  def createDeck(deckName: String, cardIds: List[Int]): Int = {
    deckId += 1
    db.put(deckId, DeckDbDTO(deckId, deckName, cardIds))
    deckId
  }

  def updateDeck(deck: DeckDbDTO): Unit = {
    db.getOrElse(deck.id, throw DeckNotFoundException(deck.id))
    db.update(deck.id, deck)
  }

  def deleteDeck(deckId: Int): Unit = {
    db.remove(deckId).getOrElse(throw DeckNotFoundException(deckId))
  }

  def getDecks: List[DeckDbDTO] = db.values.toList

  def getDeckById(deckId: Int): DeckDbDTO = db.getOrElse(deckId, throw DeckNotFoundException(deckId))

}
