package repositories.dbdtos

import models.MatchStatus

case class MatchDBDTO(matchId: Int, status: MatchStatus, matchCreatorId: String, challengedUserId: Option[String], deckId: Int)
