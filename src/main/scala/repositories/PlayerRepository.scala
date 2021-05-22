package repositories

import models.{Player, PlayerPermissions}
import routes.inputs.LoginInputs.LoginInput

import scala.collection.mutable

class PlayerRepository(db: mutable.HashMap[String, Player]) {

  def getOrCreatePlayerPermissions(loginInput: LoginInput): PlayerPermissions = {
    val player = db.get(loginInput.googleId)
    player match {
      case Some(player) => PlayerPermissions(isAuthenticated = true, isAuthorized = !player.isBlocked, isAdmin = player.isAdmin)
      case None => {
        db.put(loginInput.googleId, Player(loginInput.googleId, loginInput.name, isAdmin = false, isBlocked = false))
        PlayerPermissions(isAuthenticated = true, isAuthorized = true, isAdmin = false)
      }
    }

  }

  def getPlayerById(userId: String):Player  = {
    //todo: fix this @juli
    db.getOrElse(userId, Player("","NOT-FOUND",false,false))
  }
}
