package repositories

import models.{Player, PlayerPermissions}

import scala.collection.mutable

class PlayerRepository(db: mutable.HashMap[String,Player]) {

  def getPlayerPermissions(playerId: String): PlayerPermissions = {
    val player = db.get(playerId)
    player match {
      case Some(player) => PlayerPermissions(true,player.isBlocked,player.isAdmin)
      case None    => PlayerPermissions(false,false,false)
    }
  }

}
