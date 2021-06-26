package repositories
import models.DeckDbDTO
import repositories.daos.DeckDao

class DeckRepository(dao: DeckDao) {
  def getDeckById(deckId: Int): DeckDbDTO = {
    dao.getDeckById(deckId)
  }


  def getDecks: List[DeckDbDTO] = dao.getDecks

  def createDeck(deckName: String, cardIds: List[Int]): Int = {
    dao.createDeck(deckName,cardIds)
  }

  def updateDeck(deck: DeckDbDTO): Unit = {
    dao.updateDeck(deck)
  }

  def deleteDeck(deckId: Int): DeckDbDTO = {
    dao.deleteDeck(deckId)
  }

}
