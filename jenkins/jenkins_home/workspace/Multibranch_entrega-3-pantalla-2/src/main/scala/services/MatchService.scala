package services

import repositories.MatchRepository

class MatchService(matchRepository: MatchRepository) {

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = matchRepository.createMatch(deckId, matchCreator, challengedUser)

}
