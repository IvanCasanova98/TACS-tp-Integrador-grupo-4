package services

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import models.Events._
import models.MatchStatus.{FINISHED, IN_PROCESS, PAUSED}
import models.{AttributeName, Card, Match, PlayerScore}
import org.reactivestreams.Publisher
import org.slf4j.{Logger, LoggerFactory}
import routes.Routes.jsonParser

import scala.collection.mutable
import scala.util.Random

class MatchRoomActor(matchId: Int, matchService: MatchService) extends Actor {
  val logger: Logger = LoggerFactory.getLogger(classOf[MatchRoomActor])
  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]
  var playersReady: Set[String] = Set.empty
  var matchInfo: Option[Match] = None
  var starterPlayerId: Option[String] = None
  var playedCardIds: Set[Int] = Set.empty
  var cardsBeingPlayed: mutable.HashMap[String, Card] = mutable.HashMap.empty[String, Card]

  def getAndSaveFirstTurn: String = {
    starterPlayerId = if (matchInfo.get.status == PAUSED.name()) {
      val lastPlayerTurn = matchInfo.get.movements.maxBy(_.id).turn
      participants.keys.find(k => k != lastPlayerTurn)
    } else {
      Option(getRandomItemOfSeq(playersReady.toSeq))
    }
    starterPlayerId.get
  }

  def getTurn(playerIdTurn: String, userId: String): Turn = {
    val selectedCardForPlayer = getUnusedCard
    cardsBeingPlayed += userId -> selectedCardForPlayer
    logger.info("Sending turn to user" + userId + Turn("TURN", playerIdTurn, selectedCardForPlayer).toString)
    Turn("TURN", playerIdTurn, selectedCardForPlayer)
  }

  def getRandomItemOfSeq[T](collection: Seq[T]): T = {
    if (collection.length <= 1) return collection.head
    collection(new Random().nextInt(collection.length))
  }

  def getUnusedCard: Card = {
    val cardToPlay = getRandomItemOfSeq(matchInfo.get.deck.cards.filter(c => !playedCardIds.contains(c.id)))
    playedCardIds += cardToPlay.id
    cardToPlay
  }

  def loadPlayedCardsIfMatchWasPaused(): Unit = {
    if (matchInfo.get.status == PAUSED.name()) {
      playedCardIds = matchInfo.get.movements.flatMap(m => m.creatorCardId :: m.opponentCardId :: Nil).toSet
    }
  }

  override def receive: Receive = {
    case UserJoinedMatch(userId, actorRef) =>
      if (matchService.isUserAuthorizedToJoinMatch(matchId, userId)) {
        participants += userId -> actorRef
        logger.info(s"User $userId joined match[$matchId]")
        if (participants.size == 2) {
          var msg = "IN_LOBBY"
          participants.keys.foreach(userId => msg = msg + ":" + userId)
          broadcast(msg)
        }
      } else {
        logger.info(s"User $userId is not allowed to join match[$matchId]")
        actorRef ! s"User $userId is not allowed to join match $matchId"
      }

    case UserLeftMatch(userId) =>
      logger.info(s"User $userId left match[$matchId]")
      participants -= userId
      TextMessage(s"User $userId left match [$matchId]")
      matchService.updateMatchStatus(matchId, PAUSED.name())
      val otherPlayer = participants.find(p => p._1 != userId).get
      sendMessageToUserId("STOP_MATCH", otherPlayer._1)

    case UserAbandonMatch(userId) =>
      logger.info(s"User $userId abandoned match")
      matchService.updateMatchStatus(matchId, FINISHED.name())
      val otherPlayer = participants.find(p => p._1 != userId).get
      matchService.updateMatchWinner(matchId, otherPlayer._1)
      sendJsonToUser(MatchResult("MATCH_RESULT", otherPlayer._1),otherPlayer._1)

    case UserIsReady(userId) =>
      logger.info(s"User $userId is ready to play")
      playersReady = playersReady + userId
      val opponent = participants.keys.find(k => k != userId)
      opponent.foreach(sendMessageToUserId("OPPONENT_READY", _))
      if (playersReady.size == 2) {
        logger.info("All ready for match to start")
        //get match previous state
        matchInfo = Option(matchService.findMatchById(matchId))
        matchService.updateMatchStatus(matchId, IN_PROCESS.name())
        broadcast("ALL_READY")
      }

    case MatchInit(actorRef) =>
      val userId = participants.find(k => k._2 == actorRef).get._1
      val deckCount = matchService.getDeckCountOfMatch(matchInfo.get)
      val opponent = PlayerScore(userId = matchInfo.get.challengedPlayer.userId, userName = matchInfo.get.challengedPlayer.userName, imageUrl = matchInfo.get.challengedPlayer.imageUrl, score = matchService.getScoreOfPlayer(matchInfo.get, matchInfo.get.challengedPlayer.userId))
      val creator = PlayerScore(userId = matchInfo.get.matchCreator.userId, userName = matchInfo.get.matchCreator.userName, imageUrl = matchInfo.get.matchCreator.imageUrl, score = matchService.getScoreOfPlayer(matchInfo.get, matchInfo.get.matchCreator.userId))
      sendJsonToUser(ResponseMatchInit("INIT", deckCount, opponent, creator), userId)
      //udpate played cards if match was paused
      loadPlayedCardsIfMatchWasPaused()

      val firstTurnPlayerId = starterPlayerId.getOrElse(getAndSaveFirstTurn)
      sendJsonToUser(getTurn(firstTurnPlayerId, userId), userId)

    case MatchSetAttribute(actorRef, attribute) =>
      val userId = participants.find(k => k._2 == actorRef).get._1
      val winnerId = matchService.getMovementResult(cardsBeingPlayed, AttributeName.fromName(attribute))

      broadcastJson(MovementResult("MOVEMENT_RESULT", winnerId, AttributeName.fromName(attribute), cardsBeingPlayed.values.toList))
      logger.info(MovementResult("MOVEMENT_RESULT", winnerId, AttributeName.fromName(attribute), cardsBeingPlayed.values.toList).toString)

      //save movement of match in database (we need this saved before calculating match winner)
      matchService.saveMovement(matchId, attribute.toUpperCase(),
        cardsBeingPlayed(matchInfo.get.matchCreator.userId).id,
        cardsBeingPlayed(matchInfo.get.challengedPlayer.userId).id,
        winnerId, userId)

      val otherPlayer = participants.find(p => p._1 != userId).get
      val matchOutOfCards = (matchInfo.get.deck.cards.size - playedCardIds.size) < 2
      if (!matchOutOfCards) {
        //send next turn
        val nextTurnUserId = otherPlayer._1

        sendJsonToUser(getTurn(nextTurnUserId, userId), userId)
        sendJsonToUser(getTurn(nextTurnUserId, otherPlayer._1), otherPlayer._1)
      } else {
        val matchWinnerId = matchService.findMatchWinner(matchId, playerId = userId, otherPlayerId = otherPlayer._1)
        matchService.updateMatchStatus(matchId, FINISHED.name())
        matchService.updateMatchWinner(matchId, matchWinnerId)

        logger.info(s"Match $matchId finished. Winner id: $matchWinnerId")
        broadcastJson(MatchResult("MATCH_RESULT", matchWinnerId))
      }


    case msg => TextMessage(s"Something else arrived $msg")
  }

  def broadcast(message: String): Unit = participants.values.foreach(_ ! message)

  def broadcastJson[T](event: T): Unit = broadcast(jsonParser.writeJson(event))

  def sendMessageToUserId(message: String, userId: String): Unit = {
    participants.get(userId).foreach(_ ! message)
  }

  def sendJsonToUser[T](message: T, userId: String): Unit = {
    sendMessageToUserId(jsonParser.writeJson(message), userId)
  }
}

class MatchRoom(matchId: Int, actorSystem: ActorSystem, matchService: MatchService)(implicit val mat: Materializer) {

  private[this] val matchRoomActor = actorSystem.actorOf(Props(classOf[MatchRoomActor], matchId, matchService))

  def websocketFlow(userId: String): Flow[Message, Message, Any] = {
    val (actorRef: ActorRef, publisher: Publisher[TextMessage.Strict]) =
      Source.actorRef[String](16, OverflowStrategy.fail)
        .map(msg =>
          // outgoing message to ws
          TextMessage.Strict(msg)
        ).toMat(Sink.asPublisher(false))(Keep.both).run()

    // Announce the user has joined the match
    matchRoomActor ! UserJoinedMatch(userId, actorRef)

    val sink: Sink[Message, Any] = Flow[Message]
      .map {
        case TextMessage.Strict(msg) =>
          // incoming message from ws
          if (msg.contains("READY")) {
            val userId = msg.split(":").last
            matchRoomActor ! UserIsReady(userId)
          }
          if (msg.contains("CONNECT GAME")) {
              matchRoomActor ! MatchInit(actorRef)
          }
          if (msg.contains("SET_ATTRIBUTE")) {
            val attribute = msg.split(":").last
            matchRoomActor ! MatchSetAttribute(actorRef, attribute)
          }
          if (msg.contains("ABANDON")){
            val userId = msg.split(":").last
            matchRoomActor ! UserAbandonMatch(userId)
          }
        case keepAlive: BinaryMessage =>
          keepAlive.dataStream.runWith(Sink.ignore)
        case _ => println(s"Received something else")
      }.to(Sink.onComplete(_ =>
      // Announce the user has left the match
      matchRoomActor ! UserLeftMatch(userId)
    ))

    Flow.fromSinkAndSource(sink, Source.fromPublisher(publisher))
  }

  def sendMessage(message: TextMessage): Unit = matchRoomActor ! message

}

object MatchRoom {
  def apply(roomId: Int)(implicit actorSystem: ActorSystem, matchService: MatchService) = new MatchRoom(roomId, actorSystem, matchService)
}

class MatchRooms(actorSystem: ActorSystem, matchService: MatchService) {
  var matchRooms: Map[Int, MatchRoom] = Map.empty[Int, MatchRoom]

  def findOrCreate(number: Int): MatchRoom = matchRooms.getOrElse(number, createNewMatchRoom(number)(actorSystem, matchService))

  private def createNewMatchRoom(number: Int)(implicit actorSystem: ActorSystem, matchService: MatchService): MatchRoom = {
    val matchRoom = MatchRoom(number)
    matchRooms += number -> matchRoom
    matchRoom
  }
}
