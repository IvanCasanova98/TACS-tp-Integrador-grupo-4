package repositories.daos

import models.Movement

trait MovementDAO {
  def saveMovement(matchId: Int, creatorCardId: Int, opponentCardId: Int, turn: String, attributeName: String, winnerCardId: Int): Unit

  def getMovementsOfMatch(matchId: Int): List[Movement]

  def setAttribute(matchId: Int, attribute: String, winnerCardId: Int): Unit
}
