package repositories
import models.Deck
import repositories.daos.DeckDao

class DeckRepository(dao: DeckDao) {

  def getDecks: List[Deck] = dao.getDecks

  def createDeck(deckName: String, cardIds: List[Int]): Int = {
    dao.createDeck(deckName,cardIds)
  }

  def updateDeck(deck: Deck): Unit = {
    dao.updateDeck(deck)
  }

  def deleteDeck(deckId: Int): Deck = {
    dao.deleteDeck(deckId)
  }

}
