package repositories.daos

import exceptions.Exceptions.MatchNotFoundException
import models.CREATED
import repositories.dbdtos.MatchDBDTO

import scala.collection.mutable

class MatchLocalDAO(db: mutable.HashMap[Int, MatchDBDTO]) extends MatchDAO {
  var matchId: Int = 0

  override def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = {
    matchId += 1
    db.put(matchId, MatchDBDTO(matchId, CREATED, matchCreator, challengedUser, deckId))
    matchId
  }

  override def getMatch(matchId: Int): MatchDBDTO = db.getOrElse(matchId, throw MatchNotFoundException(matchId))

}
