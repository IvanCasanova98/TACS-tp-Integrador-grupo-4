package repositories.daos

import exceptions.Exceptions.MatchNotFoundException
import models.MatchStatus
import repositories.dbdtos.MatchDBDTO

import scala.collection.mutable

class MatchLocalDAO(db: mutable.HashMap[Int, MatchDBDTO]) extends MatchDAO {
  var matchId: Int = 0

  override def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = {
    matchId += 1
    db.put(matchId, MatchDBDTO(matchId, MatchStatus.CREATED, matchCreator, challengedUser, deckId))
    matchId
  }

  override def getMatch(matchId: Int): MatchDBDTO = db.getOrElse(matchId, throw MatchNotFoundException(matchId))

  override def updateMatchStatus(matchId: Int, status: String): Unit = {
    val matchDTO: MatchDBDTO = db.get(matchId).getOrElse(throw MatchNotFoundException(matchId))
    db.put(matchId, matchDTO.copy(status = MatchStatus.fromName(status)))
  }
}
