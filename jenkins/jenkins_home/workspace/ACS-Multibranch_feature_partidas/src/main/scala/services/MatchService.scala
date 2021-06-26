package services

import models.AttributeName.AttributeName
import models.{Attribute, Card, Match, MatchWithoutCardsAndMovements}
import repositories.dbdtos.MatchDBDTO
import repositories.{MatchRepository, MovementRepository, PlayerRepository}

import scala.util.Random

class MatchService(matchRepository: MatchRepository, playersRepo: PlayerRepository, deckService: DeckService, movementRepository: MovementRepository) {

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
    val movements = movementRepository.getMovementsOfMatch(matchId)

    Match(matchId, matchDTO.status.name(), matchCreator, challengedPlayer, deck, movements, matchDTO.winnerId)
  }

  def updateMatchStatus(matchId: Int, status: String): Unit = matchRepository.updateMatchStatus(matchId, status)

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = matchRepository.createMatch(deckId, matchCreator, challengedUser)

  def isUserAuthorizedToJoinMatch(matchId: Int, userId: String): Boolean = {
    val matchDTO = matchRepository.getMatchById(matchId)
    Seq(matchDTO.matchCreatorId, matchDTO.challengedUserId).contains(userId)
  }

  def getMovementResult(cards: Map[String, Card], chosenAttribute: AttributeName): String = {
    val firstPlayer = cards.head
    val secondPlayer = cards.last
    val firstPlayerValue = firstPlayer._2.getValueOfAttribute(chosenAttribute)
    val secondPlayerValue = secondPlayer._2.getValueOfAttribute(chosenAttribute)

    if (firstPlayerValue > secondPlayerValue)
      firstPlayer._1
    else if (firstPlayerValue < secondPlayerValue)
      secondPlayer._1
    else
      "TIE"
  }

  def saveMovement(matchId: Int, attribute: String, creatorCardId: Int, opponentCardId: Int, winnerIdOrTie: String, turn: String): Unit = {
    movementRepository.saveMovement(matchId, attribute, creatorCardId, opponentCardId, winnerIdOrTie, turn)
  }
}
