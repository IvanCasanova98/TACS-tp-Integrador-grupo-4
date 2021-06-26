package models

case class Match(id: Int,
                 status: MatchStatus,
                 matchCreator: Player,
                 challengedPlayer: Player,
                 deck: Deck,
                 movements: List[Movement],
                 winner: Option[Player])

case class Movement(attribute: Attribute, matchCreatorCard: Card, challengedPlayerCard: Card, winner: Player)

trait MatchStatus {
  def name(): String

  val matchStatus = List(CREATED, PAUSED, IN_PROCESS, FINISHED, CANCELED)

  def fromName(name: String): Option[MatchStatus] = matchStatus.find(s => s.name() == name)
}

object CREATED extends MatchStatus {
  override def name(): String = "CREATED"
}

object PAUSED extends MatchStatus {
  override def name(): String = "PAUSED"
}

object IN_PROCESS extends MatchStatus {
  override def name(): String = "IN_PROCESS"
}

object FINISHED extends MatchStatus {
  override def name(): String = "FINISHED"
}

object CANCELED extends MatchStatus {
  override def name(): String = "CANCELED"
}