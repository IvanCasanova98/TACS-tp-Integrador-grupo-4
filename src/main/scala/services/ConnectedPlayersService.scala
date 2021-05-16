package services

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, _}
import akka.stream.{Materializer, OverflowStrategy}
import models.Events.{UserJoined, UserLeft}
import org.reactivestreams.Publisher

class ConnectedPlayersActor extends Actor {

  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]
  def broadcast(message: String): Unit = participants.values.foreach(_ ! message)

  override def receive: Receive = {
    case UserJoined(name, actorRef) =>
      println(participants.mkString(""))
      println(s"User $name joined server")
      participants += name -> actorRef
      broadcast(participants.keys.toString())
    case UserLeft(name) =>
      println(s"User $name left")
      participants -= name
      broadcast(participants.keys.toString())
  }
}

class ConnectedPlayersService(actorSystem: ActorSystem)(implicit val mat: Materializer) {
  private[this] val connectedPlayersActor = actorSystem.actorOf(Props(classOf[ConnectedPlayersActor]))

  def websocketFlow(userId: String): Flow[Message, Message, Any] = {
    val (actorRef: ActorRef, publisher: Publisher[TextMessage.Strict]) =
      Source.actorRef[String](16, OverflowStrategy.fail)
        .map(msg =>
          // outgoing message to ws
          TextMessage.Strict(msg)
        ).toMat(Sink.asPublisher(false))(Keep.both).run()

    // Announce the user has joined
    connectedPlayersActor ! UserJoined(userId, actorRef)

    val sink: Sink[Message, Any] = Flow[Message]
      .map {
        case TextMessage.Strict(msg) =>
          // incoming message from ws
          println(s"Received: $msg")
        case _ => println(s"Received something else")
      }.to(Sink.onComplete(_ =>
      // Announce the user has left
      connectedPlayersActor ! UserLeft(userId)
    ))
    //Factory method allows for materialization of this Source
    Flow.fromSinkAndSource(sink, Source.fromPublisher(publisher))
  }
}

