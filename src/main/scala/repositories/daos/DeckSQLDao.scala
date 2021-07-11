package repositories.daos

import exceptions.Exceptions.DeckNotFoundException
import models.DeckDbDTO

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.collection.mutable

class DeckSQLDao(db: Connection) extends DeckDao {

  protected def rowToDeckDbDTO(row: ResultSet): DeckDbDTO = {
    val cardIdsString = row.getString("card_ids")
    val cardIds: List[Int] = cardIdsString.split(",").map(_.trim().toInt).toList

    DeckDbDTO(id = row.getInt("id"),
      name = row.getString("name"),
      cardIds = cardIds)
  }

  override def getDeckById(deckId: Int): DeckDbDTO = {
    val stmt: PreparedStatement = db.prepareStatement("SELECT id, name, card_ids FROM decks WHERE id = ?")
    stmt.setInt(1, deckId)
    val row: ResultSet = stmt.executeQuery()

    if (row.first()) {
      rowToDeckDbDTO(row)
    } else {
      throw DeckNotFoundException(deckId)
    }
  }

  override def createDeck(deckName: String, cardIds: List[Int]): Int = {
    val cardIdsString: String = cardIds.map(_.toString).mkString(",")

    val stmt = db.prepareStatement("INSERT INTO decks(name, card_ids) values (?,?)")
    stmt.setString(1, deckName)
    stmt.setString(2, cardIdsString)
    stmt.executeUpdate()
    val result = db.prepareStatement("SELECT max(id) as maxId FROM decks").executeQuery()
    if (result.next()) result.getInt("maxId") else -1
  }

  override def updateDeck(deck: DeckDbDTO): Unit = {
    val stmt = db.prepareStatement("UPDATE decks set name = ?, card_ids = ? where id = ?")
    stmt.setString(1, deck.name)
    stmt.setString(2, deck.cardIds.map(_.toString).mkString(","))
    stmt.setInt(3, deck.id)

    stmt.executeUpdate()
  }

  override def deleteDeck(deckId: Int): Unit = {
    val stmt = db.prepareStatement("UPDATE decks set deleted = true where id = ?")
    stmt.setInt(1, deckId)
    stmt.executeUpdate()
  }

  override def getDecks: List[DeckDbDTO] = {
    val stmt: PreparedStatement = db.prepareStatement("SELECT id, name, card_ids FROM decks WHERE deleted <> true")
    val result: ResultSet = stmt.executeQuery()
    val resultList: mutable.Set[DeckDbDTO] = mutable.Set()

    while (result.next()) {
      resultList += rowToDeckDbDTO(result)
    }
    resultList.toList
  }
}
