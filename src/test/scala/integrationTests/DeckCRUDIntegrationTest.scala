package integrationTests

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import models.Deck
import org.json4s.{DefaultFormats, Formats}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import repositories.DeckRepository
import repositories.daos.DeckLocalDao
import routes.DeckRoutes
import routes.inputs.DeckInputs.PartialDeckInput
import serializers.Json4sSnakeCaseSupport
import services.DeckService

import scala.collection.mutable

class DeckCRUDIntegrationTest extends WordSpec with Matchers with ScalatestRouteTest with Json4sSnakeCaseSupport with BeforeAndAfter{

  implicit val fm: Formats = DefaultFormats
  val postDeck: PartialDeckInput = PartialDeckInput("deckName", List(1,2,3,4))
  var db: mutable.HashMap[Int, Deck] = new mutable.HashMap[Int, Deck]()
  var deckDao: DeckLocalDao= new DeckLocalDao(db);
  var deckRoutes: Route = DeckRoutes(new DeckService(new DeckRepository(deckDao)))
  def postDeckEntity(partialDeckInput: PartialDeckInput): MessageEntity = Marshal(partialDeckInput).to[MessageEntity].futureValue

  before {
    db = new mutable.HashMap[Int, Deck]()
    var deckDao: DeckLocalDao= new DeckLocalDao(db);
    deckRoutes = DeckRoutes(new DeckService(new DeckRepository(deckDao)))
  }

    "Deck CRUD Test" when  {
    "Creating a deck" should {
      "Return 201 created" in {
        Post("/decks").withEntity(postDeckEntity(postDeck)) ~> deckRoutes ~> check {
          db(1).name shouldBe "deckName"
          response.status shouldBe StatusCodes.Created
        }
      }
    }
      "Updating a deck" should {
        "Return 204" in {
          Post("/decks").withEntity(postDeckEntity(postDeck)) ~> deckRoutes
          Put("/decks/1").withEntity(postDeckEntity(postDeck.copy(name = "MyDeck"))) ~> deckRoutes ~> check {
            db(1).name shouldBe "MyDeck"
            response.status shouldBe StatusCodes.NoContent
          }
        }
        "Return 404" in {
          Put("/decks/1").withEntity(postDeckEntity(postDeck)) ~> deckRoutes ~> check {
            response.status shouldBe StatusCodes.NotFound
          }
        }
      }
      "Deleting a deck" should {
        "Return 204" in {
          Post("/decks").withEntity(postDeckEntity(postDeck)) ~> deckRoutes
          Delete("/decks/1") ~> deckRoutes ~> check {
            response.status shouldBe StatusCodes.NoContent
          }
        }
        "Return 404" in {
          Delete("/decks/1") ~> deckRoutes ~> check {
            response.status shouldBe StatusCodes.NotFound
          }
        }
      }

  }

}
