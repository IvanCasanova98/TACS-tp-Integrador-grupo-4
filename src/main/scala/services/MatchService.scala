package services

import models.{Match, MatchWithoutCardsAndMovements}
import repositories.dbdtos.MatchDBDTO
import repositories.{MatchRepository, PlayerRepository}

class MatchService(matchRepository: MatchRepository, playersRepo: PlayerRepository, deckService: DeckService) {

  def findMatchesOfUser(userId: String): List[MatchWithoutCardsAndMovements] = {
    val matches: List[MatchDBDTO] = matchRepository.getMatchesOfUser(userId: String)
    matches.map { m =>
      val matchCreator = playersRepo.getPlayerById(m.matchCreatorId)
      val challengedPlayer = playersRepo.getPlayerById(m.challengedUserId)
      val deck = deckService.getDeckById(m.deckId)
      MatchWithoutCardsAndMovements(m.matchId, m.status.name(), matchCreator, challengedPlayer, deck, m.winnerId)
    }
  }

  def findMatchById(matchId: Int): Match = {
    val matchDTO = matchRepository.getMatchById(matchId)
    val matchCreator = playersRepo.getPlayerById(matchDTO.matchCreatorId)
    val challengedPlayer = playersRepo.getPlayerById(matchDTO.challengedUserId)
    val deck = deckService.getCompleteDeckById(matchDTO.deckId)

    Match(matchId, matchDTO.status.name(), matchCreator, challengedPlayer, deck, List.empty, None)
  }

  def updateMatchStatus(matchId: Int, status: String): Unit = matchRepository.updateMatchStatus(matchId, status)

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = matchRepository.createMatch(deckId, matchCreator, challengedUser)

  def isUserAuthorizedToJoinMatch(matchId: Int, userId: String): Boolean = {
    val matchDTO = matchRepository.getMatchById(matchId)
    Seq(matchDTO.matchCreatorId, matchDTO.challengedUserId).contains(userId)
  }
}
