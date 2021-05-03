package exceptions

object Exceptions {

  case class DeckNotFoundException(id: Int) extends RuntimeException(s"Deck with id $id wasn't found in database")
}
object ExceptionsSuperheroApi {
  case class NotEnoughAttribute() extends RuntimeException()
  case class UnknownException(error: String) extends RuntimeException(error)
}
