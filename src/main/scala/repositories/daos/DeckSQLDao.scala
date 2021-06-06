package repositories.daos

import exceptions.Exceptions.DeckNotFoundException
import models.DeckDbDTO

import java.sql.{Connection, PreparedStatement, ResultSet}

class DeckSQLDao(db: Connection) extends DeckDao {

  override def getDeckById(deckId: Int): DeckDbDTO = {
    val stmt: PreparedStatement = db.prepareStatement("SELECT id, name, card_ids FROM deck WHERE id = ?")
    stmt.setInt(1,deckId)
    val row: ResultSet = stmt.executeQuery()

    if (row.first()) {
      val cardIdsString = row.getString("card_ids")
      val cardIds: List[Int] = cardIdsString.split(",").map(_.toInt).toList

      DeckDbDTO(id = row.getInt("id"),
        name = row.getString("name"),
        cardIds = cardIds)
    } else {
      throw DeckNotFoundException(deckId)
    }
  }

  override def createDeck(deckName: String, cardIds: List[Int]): Int = {
    val cardIdsString: String = cardIds.map(_.toString).mkString(",")

    val stmt = db.prepareStatement("INSERT INTO deck(name, card_ids) values (?,?)")
    stmt.setString(1, deckName)
    stmt.setString(2, cardIdsString)

    stmt.executeUpdate()
  }

  override def updateDeck(deck: DeckDbDTO): Unit = ???

  override def deleteDeck(deckId: Int): DeckDbDTO = ???

  override def getDecks: List[DeckDbDTO] = ???
}
