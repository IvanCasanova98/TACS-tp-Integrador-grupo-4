package exceptions

object Exceptions {
  case class MatchNotFoundException(id: Int) extends RuntimeException(s"Match with id $id wasn't found in database")
  case class DeckNotFoundException(id: Int) extends RuntimeException(s"Deck with id $id wasn't found in database")
  case class MatchStatusNotDefinedException(status: String) extends RuntimeException(s"Status $status is not valid")
}

object ExceptionsSuperheroApi {
  case class NotEnoughAttributesException() extends RuntimeException()
  case class UnknownException(error: String) extends RuntimeException(error)
}
