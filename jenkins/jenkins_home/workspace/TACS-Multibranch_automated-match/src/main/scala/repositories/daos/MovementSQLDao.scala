package repositories.daos
import java.sql.{Connection, PreparedStatement, ResultSet}
import models.Movement
import scala.collection.mutable

class MovementSQLDao (db: Connection) extends MovementDAO {

  protected def rowToMovementDbDTO(row: ResultSet): Movement = {
    val movementId = row.getInt("id")
    val attributeName = row.getString("attribute_name")
    val creatorCardId = row.getInt("creator_card_id")
    val opponentCardId = row.getInt("opponent_card_id")
    val winnerIdOrTie = row.getString("winner_id_or_tie")
    val turn = row.getString("turn")

    Movement(id =  movementId, attributeName = attributeName, creatorCardId = creatorCardId, opponentCardId= opponentCardId, winnerIdOrTie = winnerIdOrTie, turn= turn)
  }

  def saveMovement(matchId: Int, creatorCardId: Int, opponentCardId: Int, userIdTurn: String, attributeName: String, winnerIdOrTie: String): Unit ={
    val stmt = db.prepareStatement("INSERT INTO movements(match_id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn) values (?, ?, ?, ?, ?, ?)")
    stmt.setInt(1, matchId)
    stmt.setString(2, attributeName)
    stmt.setInt(3, creatorCardId)
    stmt.setInt(4, opponentCardId)
    stmt.setString(5, winnerIdOrTie)
    stmt.setString(6, userIdTurn)
    stmt.executeUpdate()
    }
  def getMovementsOfMatch(matchId: Int): List[Movement]={
    val stmt: PreparedStatement = db.prepareStatement("SELECT id, attribute_name, creator_card_id, opponent_card_id, winner_id_or_tie, turn FROM movements WHERE match_id = ?")
    stmt.setInt(1,matchId)
    val result: ResultSet = stmt.executeQuery()
    val resultList: mutable.Set[Movement] = mutable.Set()

    while (result.next()) {
      resultList += rowToMovementDbDTO(result)
    }
    resultList.toList
  }
}
