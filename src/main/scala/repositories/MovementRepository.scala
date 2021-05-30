package repositories

import models.AttributeName.AttributeName
import models.{Attribute, AttributeName, Movement}
import repositories.daos.MovementDAO

class MovementRepository(dao: MovementDAO) {

  def saveMovement(matchId: Int, attribute: String, creatorCardId: Int, opponentCardId: Int, winnerCardId: Int, turn: String): Unit =
    dao.saveMovement(matchId, creatorCardId, opponentCardId, turn, attribute, winnerCardId)

  def getMovementsOfMatch(matchId: Int): List[Movement] = dao.getMovementsOfMatch(matchId)

  def setAttribute(matchId:Int, attribute: String, winnerCardId: Int): Unit = dao.setAttribute(matchId,attribute,winnerCardId)
}
