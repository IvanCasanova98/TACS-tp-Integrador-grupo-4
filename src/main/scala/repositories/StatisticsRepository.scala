package repositories

import models.PlayerStatistics

import java.sql.Connection

class StatisticsRepository(dbConnection: Connection) {

  def getRankings: Set[PlayerStatistics] = {
    val statement = dbConnection.prepareStatement("SELECT players.id, players.username," +
      " sum(IF(m.winner_id = players.id, 1, 0)) as won_matches, count(*) as total_matches " +
      "FROM players join matches m on players.id = m.challenged_user_id or players.id = m.creator_id" +
      " group by players.id, players.username")
    val queryResult = statement.executeQuery()
    var resultList: Set[PlayerStatistics] = Set()

    while (queryResult.next()) {
      resultList += PlayerStatistics(userId = queryResult.getString("id"),
        userName = queryResult.getString("username"),
        wonMatches = queryResult.getInt("won_matches"),
        totalMatches = queryResult.getInt("total_matches"))
    }

    resultList
  }


}
