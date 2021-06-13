package integrationTests

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import models.{MatchesStatistics, Player, PlayerStatistics}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import repositories.StatisticsRepository
import repositories.daos.{DeckSQLDao, MatchSQLDao, PlayerSQLDao}
import routes.StatisticsRoutes
import serializers.Json4sSnakeCaseSupport
import services.StatisticsService

import java.sql.Date

class StatisticsIntegrationTest extends WordSpec with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport with BeforeAndAfterAll {
  val statisticsRepository = new StatisticsRepository(H2DB())
  val statisticsService = new StatisticsService(statisticsRepository)
  val statisticsRoute: Route = StatisticsRoutes(statisticsService)
  val matchSqlDao = new MatchSQLDao(H2DB())
  val playersDao = new PlayerSQLDao(H2DB())
  val decksDao = new DeckSQLDao(H2DB())

  override def afterAll(): Unit = {
    H2DB().prepareStatement("DELETE FROM decks").execute()
    H2DB().prepareStatement("DELETE FROM players").execute()
    H2DB().prepareStatement("DELETE FROM matches").execute()
  }

  "Statistics reports" when {
    playersDao.createPlayer(Player("user1", "username1", "", false, false))
    playersDao.createPlayer(Player("user2", "username2", "", false, false))
    playersDao.createPlayer(Player("user3", "username3", "", false, false))
    decksDao.createDeck("deckName", List(1, 3, 5, 7, 4))
    val matchIdUser1vUser2 = matchSqlDao.createMatch(1, "user1", "user2")
    matchSqlDao.updateMatchStatus(matchIdUser1vUser2, "FINISHED")
    matchSqlDao.updateMatchWinner(matchIdUser1vUser2, "user2")
    val secondMatchIdUser1vUser2 = matchSqlDao.createMatch(1, "user2", "user1")
    matchSqlDao.updateMatchStatus(secondMatchIdUser1vUser2, "IN_PROCESS")
    val matchIdUser2vUser3 = matchSqlDao.createMatch(1, "user2", "user3")
    matchSqlDao.updateMatchStatus(matchIdUser2vUser3, "FINISHED")
    matchSqlDao.updateMatchWinner(matchIdUser2vUser3, "user2")
    val matchIdUser1vUser3 = matchSqlDao.createMatch(1, "user3", "user1")
    matchSqlDao.updateMatchStatus(matchIdUser1vUser3, "FINISHED")
    matchSqlDao.updateMatchWinner(matchIdUser1vUser3, "TIE")
    val secondMatchIdUser1vUser3 = matchSqlDao.createMatch(1, "user1", "user3")
    matchSqlDao.updateMatchStatus(secondMatchIdUser1vUser3, "FINISHED")
    matchSqlDao.updateMatchWinner(secondMatchIdUser1vUser3, "user3")

    "Get scoreboard ranking" should {
      "Return players statistics" in {
        Get("/statistics/rankings") ~> statisticsRoute ~> check {
          val response = responseAs[Set[PlayerStatistics]]
          val user1Statistics = response.find(_.userId == "user1").get
          user1Statistics.wonMatches shouldBe 0
          user1Statistics.totalMatches shouldBe 4

          val user2Statistics = response.find(_.userId == "user2").get
          user2Statistics.wonMatches shouldBe 2
          user2Statistics.totalMatches shouldBe 3

          val user3Statistics = response.find(_.userId == "user3").get
          user3Statistics.wonMatches shouldBe 1
          user3Statistics.totalMatches shouldBe 3
        }
      }
    }
    "Calling statistics with invalid parameters" should {
      "Return 400 bad request" in {
        Get("/statistics?from_date=2020-12-04") ~> statisticsRoute ~> check {
          response.status shouldBe StatusCodes.BadRequest
        }
      }
    }
    "Get statistics by params" should {
      "Return user 1 statistics" in {
        Get("/statistics?user_id=user1") ~> statisticsRoute ~> check {
          val user1Matches = responseAs[MatchesStatistics]
          user1Matches.total shouldBe 4
          user1Matches.finished shouldBe 3
          user1Matches.inProcess shouldBe 1
        }
      }
      "Return user 2 statistics" in {
        Get("/statistics?user_id=user2") ~> statisticsRoute ~> check {
          val user1Matches = responseAs[MatchesStatistics]
          user1Matches.total shouldBe 3
          user1Matches.finished shouldBe 2
          user1Matches.inProcess shouldBe 1
        }
      }
      "Return user 3 statistics" in {
        Get("/statistics?user_id=user3") ~> statisticsRoute ~> check {
          val user1Matches = responseAs[MatchesStatistics]
          user1Matches.total shouldBe 3
          user1Matches.finished shouldBe 3
          user1Matches.inProcess shouldBe 0
        }
      }
      "Return general statistics" in {
        Get("/statistics") ~> statisticsRoute ~> check {
          val matchesStatistics = responseAs[MatchesStatistics]
          matchesStatistics.inProcess shouldBe 1
          matchesStatistics.total shouldBe 5
          matchesStatistics.finished shouldBe 4
        }
      }
      "Return 0 in statistics if there are no matches for date interval" in {
        Get("/statistics?from_date=2020-12-01&to_date=2020-12-05") ~> statisticsRoute ~> check {
          val matchesStatistics = responseAs[MatchesStatistics]
          matchesStatistics.inProcess shouldBe 0
          matchesStatistics.total shouldBe 0
          matchesStatistics.finished shouldBe 0
        }
      }
      "Return statistics when filtered by date" in {
        val currentDate = new Date(System.currentTimeMillis())
        Get(s"/statistics?from_date=$currentDate&to_date=$currentDate") ~> statisticsRoute ~> check {
          val matchesStatistics = responseAs[MatchesStatistics]
          matchesStatistics.inProcess shouldBe 1
          matchesStatistics.total shouldBe 5
          matchesStatistics.finished shouldBe 4
        }
      }
    }
  }
}
