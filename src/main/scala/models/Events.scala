package models

import akka.actor.ActorRef
import models.AttributeName.AttributeName

object Events {

  sealed trait ConnectionEvent

  case class UserJoined(userName: String, userActor: ActorRef) extends ConnectionEvent

  case class UserLeft(userName: String) extends ConnectionEvent

  case class IncomingMessage(sender: String, message: String) extends ConnectionEvent

  case class GenericMessageToUser(message: String, userId: String) extends ConnectionEvent

  sealed trait MatchEvent

  case class UserJoinedMatch(userId: String, matchActor: ActorRef) extends MatchEvent

  case class UserLeftMatch(userId: String) extends MatchEvent

  case class UserIsReady(userId: String) extends MatchEvent

  case class MatchInit(actorRef: ActorRef) extends MatchEvent

  case class MatchSetAttribute(actorRef: ActorRef,attribute: String) extends MatchEvent

  case class ResponseMatchInit(event: String,deckCount: Int, opponent:PlayerScore, creator:PlayerScore)

  case class Turn(event: String, userIdTurn: String, card: Card)

  case class MovementResult(event: String, winnerId: String, chosenAttribute:AttributeName, winnerCard: Option[Card], loserCard: Option[Card])

  case class MatchResult()
}