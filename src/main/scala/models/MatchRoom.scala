package models

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import models.Events.{UserJoinedMatch, UserLeftMatch}
import org.reactivestreams.Publisher
import org.slf4j.{Logger, LoggerFactory}
import routes.Routes.matchService

class MatchRoomActor(matchId: Int) extends Actor {
  val logger: Logger = LoggerFactory.getLogger(classOf[MatchRoomActor])
  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case UserJoinedMatch(userId, actorRef) =>
      if (matchService.isUserAuthorizedToJoinMatch(matchId, userId)) {
        participants += userId -> actorRef
        logger.info(s"User $userId joined match[$matchId]")
        TextMessage(s"User $userId joined match $matchId")
      } else {
        logger.info(s"User $userId is not allowed to join match[$matchId]")
        TextMessage(s"User $userId is not allowed to join match $matchId")
      }

    case UserLeftMatch(userId) =>
      logger.info(s"User $userId left match[$matchId]")
      participants -= userId
      TextMessage(s"User $userId left match [$matchId]")

    case msg => TextMessage(s"Something else arrived $msg")
  }

  //def broadcast(message: TextMessage): Unit = participants.values.foreach(_ ! message)

}


class MatchRoom(matchId: Int, actorSystem: ActorSystem)(implicit val mat: Materializer) {

  private[this] val matchRoomActor = actorSystem.actorOf(Props(classOf[MatchRoomActor], matchId))

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
          println(s"Received: $msg")
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
  def apply(roomId: Int)(implicit actorSystem: ActorSystem) = new MatchRoom(roomId, actorSystem)
}

class MatchRooms(actorSystem: ActorSystem) {
  var matchRooms: Map[Int, MatchRoom] = Map.empty[Int, MatchRoom]

  def findOrCreate(number: Int): MatchRoom = matchRooms.getOrElse(number, createNewMatchRoom(number)(actorSystem))

  private def createNewMatchRoom(number: Int)(implicit actorSystem: ActorSystem): MatchRoom = {
    val matchRoom = MatchRoom(number)
    matchRooms += number -> matchRoom
    matchRoom
  }
}
