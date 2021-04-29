package services

import models.PlayerPermissions
import org.slf4j.{Logger, LoggerFactory}
import repositories.PlayerRepository

class LoginService(playerRepository: PlayerRepository) {

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def getPlayerPermissions(playerId: String): PlayerPermissions = {
    playerRepository.getPlayerPermissions(playerId)
  }

}
