package repositories

import repositories.daos.MatchDAO
import repositories.dbdtos.MatchDBDTO

class MatchRepository(dao: MatchDAO) {

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = dao.createMatch(deckId, matchCreator, challengedUser)

  def getMatchById(matchId: Int): MatchDBDTO = dao.getMatch(matchId)
}
