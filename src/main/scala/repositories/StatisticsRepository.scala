package repositories

import models.{MatchesStatistics, PlayerStatistics}

import java.sql.{Connection, Date, ResultSet}

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

  def matchesStatisticsQuery: String = "SELECT count(*) as total, sum(if(m.status = 'IN_PROCESS', 1, 0)) as in_process," +
    " sum(if(m.status = 'FINISHED', 1, 0)) as finished from matches m"

  def getMatchesStatistics: MatchesStatistics = {
    val result = dbConnection.prepareStatement(matchesStatisticsQuery).executeQuery()
    resultToMatchesStatistics(result)
  }

  private def resultToMatchesStatistics(result: ResultSet): MatchesStatistics = {
    MatchesStatistics(total = result.getInt("total"),
      inProcess = result.getInt("in_process"),
      finished = result.getInt("finished"))
  }

  def getMatchesStatisticsByUserId(userId: String): MatchesStatistics = {
    val filterByUserId = " join players p on m.creator_id = p.id or m.challenged_user_id = p.id WHERE p.id = ?"
    val statement = dbConnection.prepareStatement(matchesStatisticsQuery + filterByUserId)
    statement.setString(1, userId)
    val result = statement.executeQuery()
    resultToMatchesStatistics(result)
  }

  def getMatchesStatisticsByDate(fromDate: Date, toDate: Date): MatchesStatistics = {
    val filterByDate = " WHERE matches.created_date >= ? AND matches.created_date <= ?"
    val statement = dbConnection.prepareStatement(matchesStatisticsQuery + filterByDate)
    statement.setDate(1, fromDate)
    statement.setDate(2, toDate)
    val result = statement.executeQuery()
    resultToMatchesStatistics(result)
  }
}
