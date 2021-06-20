package models

import exceptions.Exceptions.MatchStatusNotDefinedException
import models.MatchStatus.PAUSED.matchStatus

case class Match(id: Int,
                 status: String,
                 matchCreator: Player,
                 challengedPlayer: Player,
                 deck: Deck,
                 movements: List[Movement],
                 winnerId: Option[String])

case class Movement(id: Int, attributeName: String, creatorCardId: Int, opponentCardId: Int, winnerIdOrTie: String, turn: String)

case class MatchWithoutCardsAndMovements(id: Int,
                                         status: String,
                                         matchCreator: Player,
                                         challengedPlayer: Player,
                                         deckDbDTO: DeckDbDTO,
                                         winnerId: Option[String])

case class MatchesStatistics(total: Int,
                             inProcess: Int,
                             finished: Int)

object MatchStatus {
  sealed trait MatchStatus {
    def name(): String

    val matchStatus = List(CREATED, PAUSED, IN_PROCESS, FINISHED)
  }

  def fromName(name: String): MatchStatus = matchStatus.find(s => s.name() == name).getOrElse(throw MatchStatusNotDefinedException(name))

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

}