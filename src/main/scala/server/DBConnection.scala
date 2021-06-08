package server

import java.sql.{Connection, DriverManager}

object DBConnection {
  val url = "jdbc:mysql://database:3306/tacs?allowPublicKeyRetrieval=true&useSSL=false"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "adminadmin"
  var connection: Option[Connection] = None

  private var driverLoaded = false

  private def loadDriver() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver").newInstance
      driverLoaded = true
    } catch {
      case e: Exception => {
        println("ERROR: Driver not available: " + e.getMessage)
        throw e
      }
    }
  }

  def getConnection: Connection = {
    // Only load driver first time
    this.synchronized {
      if (!driverLoaded) loadDriver()
    }

    // Get the connection
    try {
      DriverManager.getConnection(url, username, password)
    } catch {
      case e: Exception =>
        println("ERROR: No connection: " + e.getMessage)
        throw e
    }
  }
}

