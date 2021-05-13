package services

import repositories.MatchRepository

class MatchService(matchRepository: MatchRepository) {

  def createMatch(deckId: Int, matchCreator: String): Int = matchRepository.createMatch(deckId, matchCreator)

}
