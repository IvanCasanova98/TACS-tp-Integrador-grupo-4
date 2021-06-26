package repositories.daos

import models.{Player, PlayerPermissions}
import routes.inputs.LoginInputs.LoginInput

trait PlayerDao {

  def getOrCreatePlayerPermissions(loginInput: LoginInput): PlayerPermissions

  def getPlayerById(userId: String): Player

}

