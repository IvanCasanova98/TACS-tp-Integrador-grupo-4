package routes.inputs

object MatchInputs {
  case class PostMatchDTO(deckId: Int, matchCreatorId: String, challengedPlayerId: String)
  case class UpdateMatchStatus(status: String)
}
