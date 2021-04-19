package unitTests
import akka.http.scaladsl.server._
import org.scalatest.{FunSuite, Matchers, WordSpec}
import routes.Routes
import akka.http.scaladsl.testkit.ScalatestRouteTest

class TestRoutes extends WordSpec with Matchers with ScalatestRouteTest{
  "The routes" should {

    "return pong when ping is called" in {
      Get("/ping") ~> Routes.apply() ~> check {
        responseAs[String] shouldEqual "pong"
      }
    }
  }
}
