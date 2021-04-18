package routes

import akka.http.scaladsl.server.Directives.{complete, path, get}
import akka.http.scaladsl.server.Route


object Routes {

  def apply(): Route = {
    path("asd") {
      get {
        complete(200, "Happy christmas")
      }
    }
  }
}
