package repositories.daos
import models.{AttributeName, Movement}

import scala.collection.mutable

class MovementLocalDAO(db: mutable.HashMap[Int, List[Movement]]) extends MovementDAO {
  var movementId: Int = 0

  override def getMovementsOfMatch(matchId: Int): List[Movement] = {
    db.getOrElse(matchId, List.empty)
  }

  override def saveMovement(matchId: Int, attributeName: String, creatorCardId: Int, opponentCardId: Int, winnerCardId: Int): Unit = {
    val movements = db.getOrElse(matchId, List.empty)
    db.put(matchId, movements :+ Movement(movementId, attributeName, creatorCardId, opponentCardId, winnerCardId))
    movementId += 1
  }
}
