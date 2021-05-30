package repositories.daos

import models.{AttributeName, Movement}

trait MovementDAO {
  def saveMovement(matchId: Int, creatorCardId: Int, opponentCardId: Int,  turn:String, attributeName: String = null,winnerCardId: Int = null): Unit
  def getMovementsOfMatch(matchId: Int): List[Movement]
  def setAttibute(matchId:Int,attribute: String,winnerCardId: Int): Unit
}
