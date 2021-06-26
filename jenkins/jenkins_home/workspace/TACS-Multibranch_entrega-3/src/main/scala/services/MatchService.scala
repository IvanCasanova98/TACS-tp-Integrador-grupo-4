package services

import models.AttributeName.AttributeName
import models.MatchStatus.PAUSED
import models.{Card, Match, MatchWithoutCardsAndMovements, Movement}
import repositories.dbdtos.MatchDBDTO
import repositories.{MatchRepository, MovementRepository, PlayerRepository}
import routes.DeckRoutes.logger

import scala.collection.mutable

class MatchService(matchRepository: MatchRepository, playersRepo: PlayerRepository, deckService: DeckService, movementRepository: MovementRepository) {

  def getDeckCountOfMatch(matchInfo: Match): Int = {
    val cards = if (matchInfo.status == PAUSED.name()) {
      matchInfo.deck.cards.size - (matchInfo.movements.size * 2)
    } else matchInfo.deck.cards.size
    Math.floor(cards / 2).toInt
  }

  def getScoreOfPlayer(matchInfo: Match, userId: String): Int = {
    if (matchInfo.status == PAUSED.name()) {
      countWonMovements(matchInfo.movements, userId)
    } else 0
  }

  def countWonMovements(movements: List[Movement], userId: String): Int = movements.count(movement => movement.winnerIdOrTie == userId)

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

  def updateMatchWinner(matchId: Int, winnerId: String): Unit = matchRepository.updateMatchWinner(matchId, winnerId)

  def createMatch(deckId: Int, matchCreator: String, challengedUser: String): Int = matchRepository.createMatch(deckId, matchCreator, challengedUser)

  def isUserAuthorizedToJoinMatch(matchId: Int, userId: String): Boolean = {
    val matchDTO = matchRepository.getMatchById(matchId)
    Seq(matchDTO.matchCreatorId, matchDTO.challengedUserId).contains(userId)
  }

  def getMovementResult(cards: mutable.HashMap[String, Card], chosenAttribute: AttributeName): String = {
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

  def findMatchWinner(matchId: Int, playerId: String, otherPlayerId: String): String = {
    // user ID --> movements won
    val winsCounter: mutable.Map[String, Int] = mutable.Map(playerId -> 0, otherPlayerId -> 0)
    logger.info(winsCounter.toString)
    movementRepository.getMovementsOfMatch(matchId).foreach(mov => {
      if (mov.winnerIdOrTie != "TIE")  winsCounter.put(mov.winnerIdOrTie,winsCounter(mov.winnerIdOrTie).+(1))
      logger.info(winsCounter.toString)
    })
    if (winsCounter(playerId) == winsCounter(otherPlayerId)) return "TIE"
    //get userId that has most wins
    winsCounter.find(userWinCount => userWinCount._2 == winsCounter.values.max).get._1
  }

}
