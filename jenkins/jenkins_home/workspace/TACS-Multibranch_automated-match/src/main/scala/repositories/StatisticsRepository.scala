package repositories

import models.{MatchesStatistics, PlayerStatistics}

import java.sql.{Connection, Date, ResultSet}

class StatisticsRepository(dbConnection: Connection) {
  val filterByDate = " WHERE m.created_date >= ? AND m.created_date <= ?"
  val filterByUserId = " join players p on m.creator_id = p.id or m.challenged_user_id = p.id WHERE p.id = ?"

  def getRankings: Set[PlayerStatistics] = {
    val statement = dbConnection.prepareStatement("SELECT players.id, players.username," +
      " SUM(case when (m.winner_id = players.id) then 1 else 0 end) as won_matches, COUNT(*) as total_matches " +
      " FROM players join matches m on players.id = m.challenged_user_id or players.id = m.creator_id" +
      " WHERE players.id <> 'automatedPlayer'" +
      " GROUP BY players.id, players.username")
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

  def matchesStatisticsQuery: String = "SELECT count(*) as total, sum(case when (m.status = 'IN_PROCESS') then 1 else 0 end) as in_process," +
    " sum(case when (m.status = 'FINISHED') then 1 else 0 end) as finished from matches m"

  def getMatchesStatistics: MatchesStatistics = {
    val result = dbConnection.prepareStatement(matchesStatisticsQuery).executeQuery()
    resultToMatchesStatistics(result)
  }

  private def resultToMatchesStatistics(result: ResultSet): MatchesStatistics = {
    if (result.first())
      MatchesStatistics(total = result.getInt("total"),
        inProcess = result.getInt("in_process"),
        finished = result.getInt("finished"))
    else
      MatchesStatistics(0, 0, 0)
  }

  def getMatchesStatisticsByUserId(userId: String): MatchesStatistics = {
    val statement = dbConnection.prepareStatement(matchesStatisticsQuery + filterByUserId)
    statement.setString(1, userId)
    val result = statement.executeQuery()
    resultToMatchesStatistics(result)
  }

  def getMatchesStatisticsByDate(fromDate: Date, toDate: Date): MatchesStatistics = {
    val statement = dbConnection.prepareStatement(matchesStatisticsQuery + filterByDate)
    statement.setDate(1, fromDate)
    statement.setDate(2, toDate)
    val result = statement.executeQuery()
    resultToMatchesStatistics(result)
  }

  def getMatchesStatisticsByUserIdAndDate(id: String, fromDate: Date, toDate: Date): MatchesStatistics = {
    val statement = dbConnection.prepareStatement(matchesStatisticsQuery + filterByUserId + filterByDate.replace("WHERE", "AND"))
    statement.setString(1, id)
    statement.setDate(2, fromDate)
    statement.setDate(3, toDate)
    val result = statement.executeQuery()
    resultToMatchesStatistics(result)
  }

}
