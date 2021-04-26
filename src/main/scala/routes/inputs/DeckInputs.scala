package routes.inputs

object DeckInputs {

  case class PostDeckInput(name: String, cardIds: List[Int])

}
