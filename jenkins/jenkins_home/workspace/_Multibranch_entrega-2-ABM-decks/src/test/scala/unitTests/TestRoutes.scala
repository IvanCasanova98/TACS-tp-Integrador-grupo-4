package unitTests

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import routes.Routes

class TestRoutes extends WordSpec with Matchers with ScalatestRouteTest {
  "The routes" should {

    "return pong when ping is called" in {
      Get("/ping") ~> Routes.apply() ~> check {
        responseAs[String] shouldEqual "pong"
      }
    }
  }
}
