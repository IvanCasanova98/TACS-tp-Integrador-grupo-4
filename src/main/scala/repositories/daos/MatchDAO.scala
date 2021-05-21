package repositories.daos

import repositories.dbdtos.MatchDBDTO

trait MatchDAO {

  def getMatch(matchId: Int): MatchDBDTO

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int

}
