package repositories.dbdtos

import models.MatchStatus.MatchStatus

import java.util.Date

case class MatchDBDTO(matchId: Int,
                      status: MatchStatus,
                      matchCreatorId: String,
                      challengedUserId: String,
                      deckId: Int,
                      winnerId: Option[String],
                      createdDate: Date)
