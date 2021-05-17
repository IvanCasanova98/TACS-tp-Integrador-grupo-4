package repositories

import repositories.daos.MatchDAO

class MatchRepository(dao: MatchDAO) {

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = dao.createMatch(deckId, matchCreator, challengedUser)

}
