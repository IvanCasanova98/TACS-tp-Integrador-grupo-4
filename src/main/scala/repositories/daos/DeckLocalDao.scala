package repositories.daos

import exceptions.Exceptions.DeckNotFoundException
import models.Deck

import scala.collection.mutable


class DeckLocalDao(db: mutable.HashMap[Int, Deck]) extends DeckIDao {
  var deckId = 0

  def createDeck(deckName: String, cardIds: List[Int]): Int = {
    deckId += 1
    db.put(deckId, Deck(deckId, deckName, cardIds))
    deckId
  }

  def updateDeck(deck: Deck): Unit = {
    db.getOrElse(deck.id, throw DeckNotFoundException(deck.id))
    db.update(deck.id, deck)
  }

  def deleteDeck(deckId: Int): Deck = {
    db.remove(deckId).getOrElse(throw DeckNotFoundException(deckId))
  }

}
