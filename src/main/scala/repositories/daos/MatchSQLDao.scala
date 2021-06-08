package repositories.daos
import repositories.dbdtos.MatchDBDTO

class MatchSQLDao extends MatchDAO {

  override def getMatchesOfUser(userId: String): List[MatchDBDTO] = ???

  override def updateMatchStatus(matchId: Int, status: String): Unit = ???

  override def updateMatchWinner(matchId: Int, winnerId: String): Unit = ???

  override def getMatch(matchId: Int): MatchDBDTO = ???

  override def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = ???
}
