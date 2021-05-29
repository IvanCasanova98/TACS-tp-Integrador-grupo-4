package repositories

import models.AttributeName.AttributeName
import models.{Attribute, AttributeName, Movement}
import repositories.daos.MovementDAO

class MovementRepository(dao: MovementDAO) {

  def saveMovement(matchId: Int, attribute: String, creatorCardId: Int, opponentCardId: Int, winnerCardId: Int): Unit =
    dao.saveMovement(matchId, attribute, creatorCardId, opponentCardId, winnerCardId)

  def getMovementsOfMatch(matchId: Int): List[Movement] = dao.getMovementsOfMatch(matchId)
}
