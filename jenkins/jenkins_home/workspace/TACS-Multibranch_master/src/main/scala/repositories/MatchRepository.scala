package repositories

import repositories.daos.MatchDAO
import repositories.dbdtos.MatchDBDTO

class MatchRepository(dao: MatchDAO) {
  def getMatchesOfUser(userId: String): List[MatchDBDTO] = dao.getMatchesOfUser(userId)

  def updateMatchStatus(matchId: Int, status: String): Unit = dao.updateMatchStatus(matchId, status)

  def updateMatchWinner(matchId: Int, winnerId: String): Unit = dao.updateMatchWinner(matchId, winnerId)

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = dao.createMatch(deckId, matchCreator, challengedUser)

  def getMatchById(matchId: Int): MatchDBDTO = dao.getMatch(matchId)
}
