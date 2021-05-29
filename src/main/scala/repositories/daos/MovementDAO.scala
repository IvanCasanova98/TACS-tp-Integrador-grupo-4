package repositories.daos

import models.{AttributeName, Movement}

trait MovementDAO {
  def saveMovement(matchId: Int, attributeName: AttributeName, creatorCardId: Int, opponentCardId: Int, winnerCardId: Int): Unit
  def getMovementsOfMatch(matchId: Int): List[Movement]
}
