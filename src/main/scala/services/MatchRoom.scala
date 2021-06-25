package services

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import models.Events._
import models.MatchStatus.{FINISHED, IN_PROCESS, PAUSED}
import models.{AttributeName, AutomatedPlayer, Card, Match, PlayerScore}
import org.reactivestreams.Publisher
import org.slf4j.{Logger, LoggerFactory}
import routes.Routes.jsonParser
import serializers.JsonParser

import scala.collection.mutable
import scala.util.Random

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

class MatchRooms(actorSystem: ActorSystem, matchService: MatchService, jsonParser: JsonParser) {
  var matchRooms: Map[Int, MatchRoom] = Map.empty[Int, MatchRoom]

  def findOrCreate(number: Int): MatchRoom = matchRooms.getOrElse(number, createNewMatchRoom(number)(actorSystem, matchService))

  def findOrCreateAutomatedRoom(matchId: Int): MatchRoom = {
    val room = findOrCreate(matchId)
    new AutomatedPlayer(matchId)(jsonParser)
    room
  }

  private def createNewMatchRoom(number: Int)(implicit actorSystem: ActorSystem, matchService: MatchService): MatchRoom = {
    val matchRoom = MatchRoom(number)
    matchRooms += number -> matchRoom
    matchRoom
  }
}
