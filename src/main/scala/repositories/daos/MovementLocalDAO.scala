package repositories.daos
import models.{AttributeName, Movement}

import scala.collection.mutable

class MovementLocalDAO(db: mutable.HashMap[Int, List[Movement]]) extends MovementDAO {
  var movementId: Int = 0

  override def getMovementsOfMatch(matchId: Int): List[Movement] = {
    db.getOrElse(matchId, List.empty)
  }

  override def saveMovement(matchId: Int, creatorCardId: Int, opponentCardId: Int, turn:String,attributeName: String = null, winnerCardId: Int=null): Unit = {
    val movements = db.getOrElse(matchId, List.empty)
    db.put(matchId, movements :+ Movement(movementId, attributeName, creatorCardId, opponentCardId, winnerCardId, turn))
    movementId += 1
  }
  override def setAttibute(matchId:Int,attribute: String,winnerCardId: Int): Unit ={
    val movements = db.getOrElse(matchId, List.empty)
    val movement = Movement(movements.last.id, attribute,movements.last.creatorCardId, movements.last.opponentCardId,winnerCardId)
    db.put(matchId, movements.init:+movement)
  }
}
