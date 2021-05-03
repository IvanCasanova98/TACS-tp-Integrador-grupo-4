package services

import models.PlayerPermissions
import org.slf4j.{Logger, LoggerFactory}
import repositories.PlayerRepository
import routes.inputs.LoginInputs.LoginInput

class LoginService(playerRepository: PlayerRepository) {

  val logger: Logger = LoggerFactory.getLogger(classOf[LoginService])

  def getPlayerPermissions(loginInput: LoginInput): PlayerPermissions = {
    val result = playerRepository.getPlayerPermissions(loginInput)
    logger.info(s"Found player permissions for playerId '${loginInput.googleId}': $result")
    result
  }

}
