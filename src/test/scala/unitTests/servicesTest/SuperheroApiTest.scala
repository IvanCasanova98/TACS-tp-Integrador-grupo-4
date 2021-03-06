package unitTests.servicesTest

import akka.http.scaladsl.model.StatusCodes
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import exceptions.ExceptionsSuperheroApi.{NotEnoughAttributesException, UnknownStatusException}
import models.AttributeName.{HEIGHT, WEIGHT}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import routes.Utils.resource
import services.SuperheroApi

class SuperheroApiTest extends AnyWordSpecLike with Matchers with BeforeAndAfterAll {
  val port = 9290
  val wireMockServer = new WireMockServer(wireMockConfig().port(port))

  val superheroClient: SuperheroApi = SuperheroApi()
  superheroClient.uri = "http://localhost:9290/api/"

  override def beforeAll() {
    wireMockServer.start()
    WireMock.configureFor("localhost", port)
  }

  override def afterAll() {
    WireMock.reset()
    wireMockServer.stop()
  }

  "Superhero api test" when {
    "Get hero by id" should {
      "return card info ok if hero exists" in {
        stubFor(get(urlEqualTo("/api/103706338543731/1"))
          .willReturn(
            aResponse()
              .withStatus(StatusCodes.OK.intValue)
              .withBody(resource("responses/card_by_id_response.json"))))

        val card = superheroClient.getHeroById(1)
        card.id shouldBe 1
        card.powerStats.length shouldBe 8
        card.powerStats.exists(a => a.name == HEIGHT)
        card.powerStats.exists(a => a.name == WEIGHT)
        card.name shouldBe "A-Bomb"
      }
      "Ger hero by id when measures are in different units" in {
        stubFor(get(urlEqualTo("/api/103706338543731/1"))
          .willReturn(
            aResponse()
              .withStatus(StatusCodes.OK.intValue)
              .withBody(resource("responses/card_by_id_response_with_different_measures.json"))))

        val card = superheroClient.getHeroById(1)
        card.id shouldBe 1
        card.powerStats.length shouldBe 8
        card.powerStats.exists(a => a.name == HEIGHT)
        card.powerStats.exists(a => a.name == WEIGHT)
        card.name shouldBe "A-Bomb"
      }
      "Throw Not enough attributes when intelligence is not present in card" in {
        stubFor(get(urlEqualTo("/api/103706338543731/1"))
          .willReturn(
            aResponse()
              .withStatus(StatusCodes.OK.intValue)
              .withBody(resource("responses/card_by_id_without_all_attr_response.json"))))

        the[NotEnoughAttributesException] thrownBy superheroClient.getHeroById(1)
      }
    }
    "Search heroes by name" should {
      "Return some heroes when searching for partial name" in {
        stubFor(get(urlEqualTo("/api/103706338543731/search/batm"))
          .willReturn(
            aResponse()
              .withStatus(StatusCodes.OK.intValue)
              .withBody(resource("responses/cards_by_name_response.json"))))

        val cards = superheroClient.searchHeroesByName("batm")
        cards.length shouldBe 3
        cards.exists(c => c.name == "Batman")
      }
    }
    "If search returns an error" should {
      "return unexpected status exception" in {
        stubFor(get(urlEqualTo("/api/103706338543731/1"))
          .willReturn(
            aResponse()
              .withStatus(StatusCodes.OK.intValue)
              .withBody("{\"error\":\"asd\"}")))

        the[UnknownStatusException] thrownBy superheroClient.getHeroById(1)

      }
    }
  }

}
