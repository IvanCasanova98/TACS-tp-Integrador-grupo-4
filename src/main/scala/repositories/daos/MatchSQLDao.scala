package repositories.daos
import exceptions.Exceptions.MatchNotFoundException
import models.MatchStatus
import models.MatchStatus.CREATED
import repositories.dbdtos.MatchDBDTO

import java.sql.{Connection, Date, PreparedStatement, ResultSet}

class MatchSQLDao(db: Connection) extends MatchDAO {

  private def rowToMatchDbDto(resultSet: ResultSet):MatchDBDTO = {
    MatchDBDTO(matchId = resultSet.getInt("id"),
      status = MatchStatus.fromName(resultSet.getString("status")),
      matchCreatorId = resultSet.getString("creator_id"),
      challengedUserId = resultSet.getString("challenged_user_id"),
      deckId = resultSet.getInt("deck_id"),
      winnerId = Option(resultSet.getString("winner_id")),
      createdDate = resultSet.getDate("created_date")
    )
  }

  override def getMatchesOfUser(userId: String): List[MatchDBDTO] = ???

  override def updateMatchStatus(matchId: Int, status: String): Unit = {
    val stmt = db.prepareStatement("UPDATE matches set status = ? where id = ?")
    stmt.setString(1, status)
    stmt.setInt(2, matchId)

    stmt.executeUpdate()
  }

  override def updateMatchWinner(matchId: Int, winnerId: String): Unit = {
    val stmt = db.prepareStatement("UPDATE matches set winner_id = ? where id = ?")
    stmt.setString(1, winnerId)
    stmt.setInt(2, matchId)

    stmt.executeUpdate()
  }

  override def getMatch(matchId: Int): MatchDBDTO = {
    val stmt: PreparedStatement = db.prepareStatement("SELECT id, status, creator_id, challenged_user_id, deck_id, winner_id, created_date FROM matches WHERE id = ?")
    stmt.setInt(1, matchId)
    val row: ResultSet = stmt.executeQuery()

    if (row.first()) {
      rowToMatchDbDto(row)
    } else {
      throw MatchNotFoundException(matchId)
    }
  }

  override def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = {
    val stmt = db.prepareStatement("INSERT INTO matches(status, creator_id, challenged_user_id, deck_id, winner_id, created_date) values (?,?,?,?,?,?)")
    stmt.setString(1, CREATED.name())
    stmt.setString(2, matchCreator)
    stmt.setString(3, challengedUser)
    stmt.setInt(4, deckId)
    stmt.setString(5, null)
    stmt.setDate(6, new Date(System.currentTimeMillis()))
    stmt.executeUpdate()
    val result = db.prepareStatement("SELECT max(id) as maxId FROM matches").executeQuery()
    if (result.next()) result.getInt("maxId") else -1
  }
}
