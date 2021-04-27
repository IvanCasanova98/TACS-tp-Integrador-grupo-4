package exceptions

object Exceptions {

  case class DeckNotFoundException(id: Int) extends RuntimeException(s"Deck with id $id wasn't found in database")

}
