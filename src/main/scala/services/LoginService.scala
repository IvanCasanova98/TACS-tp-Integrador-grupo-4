package services

import models.PlayerPermissions
import org.slf4j.{Logger, LoggerFactory}
import repositories.PlayerRepository

class LoginService(playerRepository: PlayerRepository) {

  val logger: Logger = LoggerFactory.getLogger(classOf[LoginService])

  def getPlayerPermissions(playerId: String): PlayerPermissions = {
    val result = playerRepository.getPlayerPermissions(playerId)
    logger.info(s"Found player permissions for playerId '$playerId': $result")
    result
  }

}
