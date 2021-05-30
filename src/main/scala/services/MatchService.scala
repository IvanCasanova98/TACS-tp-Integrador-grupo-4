package services

import models.{Attribute, Match, MatchWithoutCardsAndMovements}
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
 /* def whoWon(matchId: Int, attribute: String):Int= {
    val lastMovement = movementRepository.getMovementsOfMatch(matchId).last
    val scoreCreator = superheroApi.get_hero_by_id(lastMovement.creatorCardId).powerStats.find(attr=>attr.name.name()==attribute).get.value
    val cardOpponent = superheroApi.get_hero_by_id(lastMovement.opponentCardId).powerStats.find(attr=>attr.name.name()==attribute).get.value
    if(scoreCreator>cardOpponent){
      return lastMovement.creatorCardId
    }
    if (scoreCreator<cardOpponent){
      return lastMovement.opponentCardId
    }
  }

  def nextCards(matchId:Int): (Int,Int) ={
    val deckId = matchRepository.getMatchById(matchId).deckId
    val cardsId = deckService.getDeckById(deckId).cardIds
    val movements = movementRepository.getMovementsOfMatch(matchId)
    val cardsIdCreators = movements.map(move=>move.creatorCardId)
    val cardsIdOpponents = movements.map(move=>move.opponentCardId)
    val cardsIdUsed = cardsIdCreators ++ cardsIdOpponents
    var cardsNotUsed = cardsId.diff(cardsIdUsed)
    val random = new Random()
    var index = random.nextInt(cardsNotUsed.size)
    val nextCardsIdCeator = cardsNotUsed(index)
    cardsNotUsed = cardsNotUsed.drop(index)
    index = random.nextInt(cardsNotUsed.size)
    val nextCardIdOpponent = cardsNotUsed(index)
    ( nextCardsIdCeator, nextCardIdOpponent)
  }*/
}
