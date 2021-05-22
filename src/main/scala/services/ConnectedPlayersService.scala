package services

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, _}
import akka.stream.{Materializer, OverflowStrategy}
import models.Events.{UserJoined, UserLeft}
import org.reactivestreams.Publisher
import play.api.libs.json.{Json, Writes}
import routes.Routes.playerRepository


case class PlayerDTO(userId: String, userName:String)

class ConnectedPlayersActor extends Actor {

  var participantsActors: Map[String, ActorRef] = Map.empty[String, ActorRef]
  var playersConnected: Map[String, PlayerDTO] = Map.empty[String, PlayerDTO]

  def broadcast(users: List[PlayerDTO]): Unit = {
    //no se donde meter esto :)
    implicit val PlayerDTO = new Writes[PlayerDTO] {
      def writes(player: PlayerDTO) = Json.obj(
        "user_name" -> player.userName,
        "user_id" -> player.userId
      )
    }

    var usersAsStringList: List[String] = List()
    users.foreach(p =>
      usersAsStringList = usersAsStringList :+ Json.stringify(Json.toJson(p))
    )

    participantsActors.values.foreach(_ ! Json.stringify(Json.toJson(usersAsStringList)))
  }

  override def receive: Receive = {
    case UserJoined(userId, actorRef) =>
      println(s"User $userId joined server")
      val foundPlayer = playerRepository.getPlayerById(userId)

      participantsActors += userId -> actorRef
      playersConnected += userId -> PlayerDTO(userId = foundPlayer.userId, userName = foundPlayer.userName)

      broadcast(playersConnected.values.toList)

    case UserLeft(userId) =>
      println(s"User $userId left")
      participantsActors -= userId
      playersConnected -= userId

      broadcast(playersConnected.values.toList)
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

