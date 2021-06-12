package services

import models.PlayerStatistics
import repositories.StatisticsRepository

class StatisticsService(statisticsRepository: StatisticsRepository) {
  def getRanking: Set[PlayerStatistics] = statisticsRepository.getRankings


}
