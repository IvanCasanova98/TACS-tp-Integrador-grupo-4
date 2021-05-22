package models

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.Flow

/*
class MatchRoomActor(roomId: Int) extends Actor {

  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
   case UserJoined(name, actorRef) =>
      participants += name -> actorRef
      broadcast(SystemMessage(s"User $name joined channel..."))
      println(s"User $name joined channel[$roomId]")

    case UserLeft(name) =>
      println(s"User $name left channel[$roomId]")
      broadcast(SystemMessage(s"User $name left channel[$roomId]"))
      participants -= name

    case msg: IncomingMessage =>
      broadcast(msg)
  }

  def broadcast(message: ChatMessage): Unit = participants.values.foreach(_ ! message)

}


class MatchRoom(roomId: Int, actorSystem: ActorSystem) {

  private[this] val matchRoomActor = actorSystem.actorOf(Props(classOf[MatchRoomActor], roomId))

  def websocketFlow(user: String): Flow[Message, Message, _] = ???

  //def sendMessage(message: ChatMessage): Unit = matchRoomActor ! message

}

object MatchRoom {
  def apply(roomId: Int)(implicit actorSystem: ActorSystem) = new MatchRoom(roomId, actorSystem)
}

object MatchRooms {
  var matchRooms: Map[Int, MatchRoom] = Map.empty[Int, MatchRoom]

  def findOrCreate(number: Int)(implicit actorSystem: ActorSystem): MatchRoom = matchRooms.getOrElse(number, createNewMatchRoom(number))

  private def createNewMatchRoom(number: Int)(implicit actorSystem: ActorSystem): MatchRoom = {
    val matchRoom = MatchRoom(number)
    matchRooms += number -> matchRoom
    matchRoom
  }
}

*/