package server

import java.sql.{Connection, DriverManager}

object DBConnection {
  var url = "jdbc:mysql://localhost:3306/tacs?allowPublicKeyRetrieval=true&useSSL=false"
  if(sys.env.contains("SCOPE")){
    url = "jdbc:mysql://database:3306/tacs?allowPublicKeyRetrieval=true&useSSL=false&autoReconnect=true"
  }
  val driver = "com.mysql.jdbc.Driver"
  val username = "superfriends_app"
  val password = "batman"
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

