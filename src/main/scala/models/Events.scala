package models

import akka.actor.ActorRef

object Events {

  sealed trait ConnectionEvent

  case class UserJoined(userName: String, userActor: ActorRef) extends ConnectionEvent

  case class UserLeft(userName: String) extends ConnectionEvent

  case class IncomingMessage(sender: String, message: String) extends ConnectionEvent


}
