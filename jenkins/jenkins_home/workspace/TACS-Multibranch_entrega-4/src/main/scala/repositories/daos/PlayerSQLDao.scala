package repositories.daos

import exceptions.Exceptions.DeckNotFoundException
import models.{DeckDbDTO, Player, PlayerPermissions}
import routes.inputs.LoginInputs.LoginInput

import java.sql.{Connection, PreparedStatement, ResultSet}

class PlayerSQLDao(db: Connection) extends PlayerDao {

  protected def rowToPlayer(row: ResultSet): Player = {
    Player(userId = row.getString("id"),
      userName = row.getString("username"),
      imageUrl = row.getString("image_url"),
      isAdmin = row.getBoolean("is_admin"),
      isBlocked = row.getBoolean("is_blocked"))
  }
  override def getOrCreatePlayerPermissions(loginInput: LoginInput): PlayerPermissions = {
    var player = getPlayerById(loginInput.googleId)

    if (player.userName == "NOT-FOUND") {
      player = Player(loginInput.googleId,loginInput.name,loginInput.imageUrl,isAdmin = true, isBlocked = false)
      createPlayer(player)
    }

    PlayerPermissions(true, !player.isBlocked, player.isAdmin)
  }

  def createPlayer(player: Player): Unit = {
    val stmt = db.prepareStatement("INSERT INTO players(id,username,image_url,is_admin,is_blocked) values (?,?,?,?,?)")
    stmt.setString(1, player.userId)
    stmt.setString(2, player.userName)
    stmt.setString(3, player.imageUrl)
    stmt.setBoolean(4, player.isAdmin)
    stmt.setBoolean(5, player.isBlocked)

    stmt.executeUpdate()
  }
  override def getPlayerById(userId: String): Player ={
    val stmt: PreparedStatement = db.prepareStatement("SELECT id,username, image_url, is_admin, is_blocked FROM players WHERE id = ?")
    stmt.setString(1, userId)
    val row: ResultSet = stmt.executeQuery()

    if (row.first()) {
      rowToPlayer(row)
    } else {
      Player(userId, "NOT-FOUND","", false, false)
    }
  }
}
