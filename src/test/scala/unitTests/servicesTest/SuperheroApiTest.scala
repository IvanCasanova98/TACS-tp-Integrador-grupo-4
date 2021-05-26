package unitTests.servicesTest

import akka.http.scaladsl.model.StatusCodes
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import exceptions.ExceptionsSuperheroApi.NotEnoughAttributesException
import models.{HEIGHT, WEIGHT}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import routes.Utils.resource
import services.SuperheroApi

class SuperheroApiTest extends WordSpec with Matchers with BeforeAndAfterAll {
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

        val card = superheroClient.get_hero_by_id(1)
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

        the[NotEnoughAttributesException] thrownBy superheroClient.get_hero_by_id(1)
      }
    }
    "Search heroes by name" should {
      "Return some heroes when searching for partial name" in {
        stubFor(get(urlEqualTo("/api/103706338543731/search/batm"))
          .willReturn(
            aResponse()
              .withStatus(StatusCodes.OK.intValue)
              .withBody(resource("responses/cards_by_name_response.json"))))

        val cards = superheroClient.search_heroes_by_name("batm")
        cards.length shouldBe 3
        cards.exists(c => c.name == "Batman")
      }
    }
  }

}
