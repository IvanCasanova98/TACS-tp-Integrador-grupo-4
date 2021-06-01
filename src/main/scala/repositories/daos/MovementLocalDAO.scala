package repositories.daos

import models.{AttributeName, Movement}

import scala.collection.mutable

class MovementLocalDAO(db: mutable.HashMap[Int, List[Movement]]) extends MovementDAO {
  var movementId: Int = db.keys.size

  override def getMovementsOfMatch(matchId: Int): List[Movement] = {
    db.getOrElse(matchId, List.empty)
  }

  override def saveMovement(matchId: Int, creatorCardId: Int, opponentCardId: Int, userIdTurn: String, attributeName: String, winnerIdOrTie: String): Unit = {
    val movements = db.getOrElse(matchId, List.empty)
    db.put(matchId, movements :+ Movement(movementId, attributeName, creatorCardId, opponentCardId, winnerIdOrTie, userIdTurn))
    movementId += 1
  }

}
