package models

import akka.actor.ActorRef

object Events {

  sealed trait ConnectionEvent

  case class UserJoined(userName: String, userActor: ActorRef) extends ConnectionEvent

  case class UserLeft(userName: String) extends ConnectionEvent

  case class IncomingMessage(sender: String, message: String) extends ConnectionEvent

  case class GenericMessageToUser(message: String, userId: String) extends ConnectionEvent

  sealed trait MatchEvent

  case class UserJoinedMatch(userId: String, matchActor: ActorRef) extends MatchEvent

  case class UserLeftMatch(userId: String) extends MatchEvent

}