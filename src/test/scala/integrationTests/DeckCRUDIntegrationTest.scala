package integrationTests

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.H2DB
import org.json4s.{DefaultFormats, Formats}
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import repositories.DeckRepository
import repositories.daos.DeckSQLDao
import routes.DeckRoutes
import routes.inputs.DeckInputs.PartialDeckInput
import serializers.Json4sSnakeCaseSupport
import services.{DeckService, SuperheroApi}
import java.sql.Connection

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}

class DeckCRUDIntegrationTest extends AnyWordSpecLike with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport with BeforeAndAfter {

  implicit val fm: Formats = DefaultFormats
  val postDeck: PartialDeckInput = PartialDeckInput("deckName", List(1, 2, 3, 4))
  val db: Connection = H2DB()
  val deckDaoTest: DeckSQLDao = new DeckSQLDao(db)
  val deckRepo = new DeckRepository(deckDaoTest)
  var deckRoutes: Route = DeckRoutes(new DeckService(deckRepo, SuperheroApi()))
  val authorization: Authorization = Authorization(OAuth2BearerToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJnb29nbGVJZCI6IjExNTc0ODAyODM4NzA3OTU0ODc1NyIsImlzQXV0aGVudGljYXRlZCI6dHJ1ZSwiaXNBdXRob3JpemVkIjp0cnVlLCJpc0FkbWluIjp0cnVlLCJleHAiOjExNjI0ODM1Mjc5LCJpYXQiOjE2MjQ4MzUyODB9.77-977-9zb4i77-977-977-977-977-9eWDvv73bqu-_vVPvv70B77-9De-_vcuK77-977-977-9Vg02MA"))

  def postDeckEntity(partialDeckInput: PartialDeckInput): MessageEntity = Marshal(partialDeckInput).to[MessageEntity].futureValue

  "Deck CRUD Test" when {
    "Creating a deck" should {
      "Return 201 created" in {
        Post("/decks").withEntity(postDeckEntity(postDeck)).addHeader(authorization) ~> deckRoutes ~> check {
          val id = responseAs[Int]
          deckRepo.getDeckById(id).name shouldEqual "deckName"
          response.status shouldEqual StatusCodes.Created
        }
      }
    }
    "Updating a deck" should {
      "Return 204" in {
        Post("/decks").withEntity(postDeckEntity(postDeck)).addHeader(authorization) ~> deckRoutes ~> check {
          val id = responseAs[Int]
          Put(s"/decks/$id").withEntity(postDeckEntity(postDeck.copy(name = "MyDeck"))).addHeader(authorization) ~> deckRoutes ~> check {
            deckRepo.getDeckById(id).name shouldEqual "MyDeck"
            response.status shouldEqual StatusCodes.NoContent
          }
        }
      }
      "Return 404" in {
        Put("/decks/14").withEntity(postDeckEntity(postDeck)).addHeader(authorization) ~> deckRoutes ~> check {
          response.status shouldEqual StatusCodes.NotFound
        }
      }
    }
    "Deleting a deck" should {
      "Return 204" in {
        Post("/decks").withEntity(postDeckEntity(postDeck)).addHeader(authorization) ~> deckRoutes ~> check {
          val id = responseAs[Int]
          Delete(s"/decks/$id").addHeader(authorization) ~> deckRoutes ~> check {
            response.status shouldEqual StatusCodes.NoContent
          }
        }
      }
      "Return 404" in {
        Delete("/decks/1879685465").addHeader(authorization) ~> deckRoutes ~> check {
          response.status shouldEqual StatusCodes.NotFound
        }
      }
    }

  }

}
