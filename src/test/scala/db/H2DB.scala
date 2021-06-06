package db

import java.sql.{Connection, DriverManager, SQLException}

object H2DB {
  val url = "jdbc:h2:mem:;INIT=RUNSCRIPT FROM '~/IdeaProjects/TACS-tp-Integrador-grupo-4/src/test/resources/superamigos.sql'"
  val JDBC_DRIVER = "org.h2.Driver"
  var con: Connection = DriverManager.getConnection(url)

  def apply(): Connection = {

    try {
      Class.forName(JDBC_DRIVER);
      val stm = con.createStatement
      val rs = stm.executeQuery("SELECT 1+1")
      try if (rs.next) System.out.println(rs.getInt(1))
      catch {
        case ex: SQLException => println(ex.getMessage)
      }
    }
    con
  }

  def close(): Unit = if (con != null) con.close()

}
