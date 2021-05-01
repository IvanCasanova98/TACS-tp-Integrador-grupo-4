package unitTests

import akka.actor.typed.ActorRef
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.sun.tools.javac.jvm.PoolConstant.LoadableConstant.String
import org.scalatest.{Matchers, WordSpec}
import routes.Routes

class TestRoutes extends WordSpec with Matchers with ScalatestRouteTest {
  "The routes" should {

    "return pong when ping is called" in {
      Get("/ping") ~> Routes.apply() ~> check {
        responseAs[String].substring(1,responseAs[String].length()-1) shouldEqual "pong"
      }
    }
  }
}
