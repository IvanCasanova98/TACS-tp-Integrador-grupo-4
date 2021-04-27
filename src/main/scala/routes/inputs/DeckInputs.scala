package routes.inputs

object DeckInputs {

  case class PartialDeckInput(name: String, cardIds: List[Int])

}
