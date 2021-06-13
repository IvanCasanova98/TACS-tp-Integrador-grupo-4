package unitTests.daoTests

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import models.{Player, PlayerPermissions}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import repositories.daos.{DeckSQLDao, PlayerSQLDao}
import routes.inputs.LoginInputs.LoginInput

import java.sql.Connection

class PlayerSQLDaoTest extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {
  var db: Connection = H2DB()
  val playerDao: PlayerSQLDao = new PlayerSQLDao(db)

  val examplePlayer: Player = Player("id123-123", "fg", "boquita.jpg", true, false)
  val exampleLoginInput: LoginInput = LoginInput("fg","fg@boke.com","boke.jpg","id123-123","tokenIDID")

  "Player SQL Dao" when {
    "Creating players" should {
      "create player ok for example player" in {
        playerDao.createPlayer(examplePlayer)

        val foundPlayer: Player = playerDao.getPlayerById("id123-123")
        foundPlayer shouldBe examplePlayer
      }
    }

    "Getting player permissions" should {
      "get permission ok for example player" in {
        val foundPlayerPermissions: PlayerPermissions = playerDao.getOrCreatePlayerPermissions(exampleLoginInput)

        foundPlayerPermissions shouldBe PlayerPermissions(isAuthenticated = true,isAuthorized = true,isAdmin = true)
      }

    }

  }
}
