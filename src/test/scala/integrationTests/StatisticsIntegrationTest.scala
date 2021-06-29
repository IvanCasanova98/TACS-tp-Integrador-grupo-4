/*package integrationTests

import java.sql.{Connection, Date}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import models.{MatchesStatistics, Player, PlayerStatistics}
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import repositories.StatisticsRepository
import repositories.daos.{DeckSQLDao, MatchSQLDao, PlayerSQLDao}
import routes.StatisticsRoutes
import serializers.Json4sSnakeCaseSupport
import services.StatisticsService

class StatisticsIntegrationTest extends AnyWordSpecLike with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport with BeforeAndAfter {
  val db: Connection = H2DB()
  val statisticsRepository = new StatisticsRepository(db)
  val statisticsService = new StatisticsService(statisticsRepository)
  val statisticsRoute: Route = StatisticsRoutes(statisticsService)
  val matchSqlDao = new MatchSQLDao(db)
  val playersDao = new PlayerSQLDao(db)
  val decksDao = new DeckSQLDao(db)
  val authorization: Authorization = Authorization(OAuth2BearerToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJnb29nbGVJZCI6IjExNTc0ODAyODM4NzA3OTU0ODc1NyIsImlzQXV0aGVudGljYXRlZCI6dHJ1ZSwiaXNBdXRob3JpemVkIjp0cnVlLCJpc0FkbWluIjp0cnVlLCJleHAiOjExNjI0ODM1Mjc5LCJpYXQiOjE2MjQ4MzUyODB9.77-977-9zb4i77-977-977-977-977-9eWDvv73bqu-_vVPvv70B77-9De-_vcuK77-977-977-9Vg02MA"))

  before {
    H2DB.resetTables(db)

    playersDao.createPlayer(Player("user1", "username1", "", false, false))
    playersDao.createPlayer(Player("user2", "username2", "", false, false))
    playersDao.createPlayer(Player("user3", "username3", "", false, false))
    val deckId = decksDao.createDeck("deckName", List(1, 3, 5, 7, 4))
    val matchIdUser1vUser2 = matchSqlDao.createMatch(deckId, "user1", "user2")
    matchSqlDao.updateMatchStatus(matchIdUser1vUser2, "FINISHED")
    matchSqlDao.updateMatchWinner(matchIdUser1vUser2, "user2")
    val secondMatchIdUser1vUser2 = matchSqlDao.createMatch(deckId, "user2", "user1")
    matchSqlDao.updateMatchStatus(secondMatchIdUser1vUser2, "IN_PROCESS")
    val matchIdUser2vUser3 = matchSqlDao.createMatch(deckId, "user2", "user3")
    matchSqlDao.updateMatchStatus(matchIdUser2vUser3, "FINISHED")
    matchSqlDao.updateMatchWinner(matchIdUser2vUser3, "user2")
    val matchIdUser1vUser3 = matchSqlDao.createMatch(deckId, "user3", "user1")
    matchSqlDao.updateMatchStatus(matchIdUser1vUser3, "FINISHED")
    matchSqlDao.updateMatchWinner(matchIdUser1vUser3, "TIE")
    val secondMatchIdUser1vUser3 = matchSqlDao.createMatch(deckId, "user1", "user3")
    matchSqlDao.updateMatchStatus(secondMatchIdUser1vUser3, "FINISHED")
    matchSqlDao.updateMatchWinner(secondMatchIdUser1vUser3, "user3")


  }

  after{
      H2DB.resetTables(db)
    }

  "Statistics reports" when {

    "Get scoreboard ranking" should {
      "Return players statistics" in {
        Get("/statistics/rankings").addHeader(authorization) ~> statisticsRoute ~> check {
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
        Get("/statistics?from_date=2020-12-04").addHeader(authorization) ~> statisticsRoute ~> check {
          response.status shouldBe StatusCodes.BadRequest
        }
      }
    }
    "Get statistics by params" should {
      "Return user 1 statistics" in {
        Get("/statistics?user_id=user1").addHeader(authorization) ~> statisticsRoute ~> check {
          val user1Matches = responseAs[MatchesStatistics]
          user1Matches.total shouldBe 4
          user1Matches.finished shouldBe 3
          user1Matches.inProcess shouldBe 1
        }
      }
      "Return user 2 statistics" in {
        Get("/statistics?user_id=user2").addHeader(authorization) ~> statisticsRoute ~> check {
          val user1Matches = responseAs[MatchesStatistics]
          user1Matches.total shouldBe 3
          user1Matches.finished shouldBe 2
          user1Matches.inProcess shouldBe 1
        }
      }
      "Return user 3 statistics" in {
        Get("/statistics?user_id=user3").addHeader(authorization) ~> statisticsRoute ~> check {
          val user1Matches = responseAs[MatchesStatistics]
          user1Matches.total shouldBe 3
          user1Matches.finished shouldBe 3
          user1Matches.inProcess shouldBe 0
        }
      }
      "Return user statistics by date" in {
        val currentDate = new Date(System.currentTimeMillis())
        Get(s"/statistics?user_id=user3&from_date=$currentDate&to_date=$currentDate").addHeader(authorization) ~> statisticsRoute ~> check {
          val user1Matches = responseAs[MatchesStatistics]
          user1Matches.total shouldBe 3
          user1Matches.finished shouldBe 3
          user1Matches.inProcess shouldBe 0
        }
      }
      "Return general statistics" in {
        Get("/statistics").addHeader(authorization) ~> statisticsRoute ~> check {
          val matchesStatistics = responseAs[MatchesStatistics]
          matchesStatistics.inProcess shouldBe 1
          matchesStatistics.total shouldBe 5
          matchesStatistics.finished shouldBe 4
        }
      }
      "Return 0 in statistics if there are no matches for date interval" in {
        Get("/statistics?from_date=2020-12-01&to_date=2020-12-05").addHeader(authorization) ~> statisticsRoute ~> check {
          val matchesStatistics = responseAs[MatchesStatistics]
          matchesStatistics.inProcess shouldBe 0
          matchesStatistics.total shouldBe 0
          matchesStatistics.finished shouldBe 0
        }
      }
      "Return statistics when filtered by date" in {
        val currentDate = new Date(System.currentTimeMillis())
        Get(s"/statistics?from_date=$currentDate&to_date=$currentDate").addHeader(authorization) ~> statisticsRoute ~> check {
          val matchesStatistics = responseAs[MatchesStatistics]
          matchesStatistics.inProcess shouldBe 1
          matchesStatistics.total shouldBe 5
          matchesStatistics.finished shouldBe 4
        }
      }
    }
  }
}
/*