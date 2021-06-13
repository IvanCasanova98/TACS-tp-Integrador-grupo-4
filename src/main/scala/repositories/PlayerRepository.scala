package repositories

import models.{Player, PlayerPermissions}
import repositories.daos.PlayerDao
import routes.inputs.LoginInputs.LoginInput

import scala.collection.mutable

class PlayerRepository(dao: PlayerDao) {

  def getOrCreatePlayerPermissions(loginInput: LoginInput): PlayerPermissions = dao.getOrCreatePlayerPermissions(loginInput)

  def getPlayerById(userId: String): Player = dao.getPlayerById(userId)
}
