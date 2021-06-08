package repositories.daos
import models.{Player, PlayerPermissions}
import routes.inputs.LoginInputs

import scala.collection.mutable

class PlayerLocalDao(db: mutable.HashMap[String, Player]) extends PlayerDao {
  override def getOrCreatePlayerPermissions(loginInput: LoginInputs.LoginInput): PlayerPermissions = {

    val player = db.get(loginInput.googleId)
    player match {
      case Some(player) => PlayerPermissions(isAuthenticated = true, isAuthorized = !player.isBlocked, isAdmin = player.isAdmin)
      case None => {
        db.put(loginInput.googleId, Player(loginInput.googleId, loginInput.name, isAdmin = true, isBlocked = false, imageUrl = loginInput.imageUrl))
        PlayerPermissions(isAuthenticated = true, isAuthorized = true, isAdmin = true)
      }
    }
  }

  override def getPlayerById(userId: String): Player =  db.getOrElse(userId, Player(userId, "NOT-FOUND","", false, false))

  }
