package repositories.daos

trait MatchDAO {

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int

}
