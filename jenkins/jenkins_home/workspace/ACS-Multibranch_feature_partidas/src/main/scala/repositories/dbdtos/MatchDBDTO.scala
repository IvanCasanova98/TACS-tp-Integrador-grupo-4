package repositories.dbdtos

import models.MatchStatus.MatchStatus

case class MatchDBDTO(matchId: Int,
                      status: MatchStatus,
                      matchCreatorId: String,
                      challengedUserId: String,
                      deckId: Int,
                      winnerId: Option[String])
