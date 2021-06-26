package repositories.daos

import repositories.dbdtos.MatchDBDTO

trait MatchDAO {
  def getMatchesOfUser(userId: String): List[MatchDBDTO]

  def updateMatchStatus(matchId: Int, status: String): Unit

  def updateMatchWinner(matchId: Int, winnerId: String): Unit

  def getMatch(matchId: Int): MatchDBDTO

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int

}
