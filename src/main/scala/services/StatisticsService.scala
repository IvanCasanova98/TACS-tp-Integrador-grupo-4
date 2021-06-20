package services

import exceptions.Exceptions.InvalidQueryParamsException
import models.{MatchesStatistics, PlayerStatistics}
import repositories.StatisticsRepository

import java.sql.Date

class StatisticsService(statisticsRepository: StatisticsRepository) {

  def getMatchesStatistics(userId: Option[String], fromDate: Option[Date], toDate: Option[Date]): MatchesStatistics = {
    (userId, fromDate, toDate) match {
      case (None, None, None) => statisticsRepository.getMatchesStatistics
      case (Some(id), None, None) => statisticsRepository.getMatchesStatisticsByUserId(id)
      case (None, Some(fromDate), Some(toDate)) => statisticsRepository.getMatchesStatisticsByDate(fromDate, toDate)
      case (Some(id), Some(fromDate), Some(toDate)) => statisticsRepository.getMatchesStatisticsByUserIdAndDate(id, fromDate, toDate)
      case _ => throw InvalidQueryParamsException()
    }
  }

  def getRanking: Set[PlayerStatistics] = statisticsRepository.getRankings


}
