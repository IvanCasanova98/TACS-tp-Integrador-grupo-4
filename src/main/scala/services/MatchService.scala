package services

import repositories.MatchRepository

class MatchService(matchRepository: MatchRepository) {
  def updateMatchStatus(matchId: Int, status: String): Unit = matchRepository.updateMatchStatus(matchId, status)

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = matchRepository.createMatch(deckId, matchCreator, challengedUser)

  def isUserAuthorizedToJoinMatch(matchId: Int, userId: String): Boolean = {
    val matchDTO = matchRepository.getMatchById(matchId)
    Seq(matchDTO.matchCreatorId, matchDTO.challengedUserId).contains(userId)
  }
}
