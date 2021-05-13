package repositories.daos

trait MatchDAO {

  def createMatch(deckId: Int, matchCreator: String): Int

}
