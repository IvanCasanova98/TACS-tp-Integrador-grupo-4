package repositories

import repositories.daos.MatchDAO

class MatchRepository(dao: MatchDAO) {

  def createMatch(deckId: Int, matchCreator: String): Int = dao.createMatch(deckId, matchCreator)

}
