package db

import exceptions.Exceptions.SqlConnectionException

import java.sql.{Connection, DriverManager, SQLException}

object H2DB {
  val url = "jdbc:h2:mem:;INIT=RUNSCRIPT FROM './../TACS-tp-Integrador-grupo-4/src/test/resources/superamigos.sql'"
  val JDBC_DRIVER = "org.h2.Driver"
  var con: Connection = DriverManager.getConnection(url)

  def apply(): Connection = {
    try {
      Class.forName(JDBC_DRIVER)
      con
    } catch {
      case ex: SQLException => throw SqlConnectionException(ex)
    }
  }

  def close(): Unit = if (con != null) con.close()

}
