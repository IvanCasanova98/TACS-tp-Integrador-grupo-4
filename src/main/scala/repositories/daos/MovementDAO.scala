package repositories.daos

import models.Movement

trait MovementDAO {
  def saveMovement(matchId: Int, creatorCardId: Int, opponentCardId: Int, userIdTurn: String, attributeName: String, winnerIdOrTie: String): Unit

  def getMovementsOfMatch(matchId: Int): List[Movement]
}
