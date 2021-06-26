package repositories

import models.AttributeName.AttributeName
import models.{Attribute, AttributeName, Movement}
import repositories.daos.MovementDAO

class MovementRepository(dao: MovementDAO) {

  def saveMovement(matchId: Int, attribute: String, creatorCardId: Int, opponentCardId: Int, winnerIdOrTie: String, turn: String): Unit =
    dao.saveMovement(matchId, creatorCardId, opponentCardId, turn, attribute, winnerIdOrTie)

  def getMovementsOfMatch(matchId: Int): List[Movement] = dao.getMovementsOfMatch(matchId)
}
