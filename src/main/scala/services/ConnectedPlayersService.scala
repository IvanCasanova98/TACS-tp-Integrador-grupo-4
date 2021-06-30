package services

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.scaladsl.{Flow, _}
import akka.stream.{Materializer, OverflowStrategy}
import models.Events.{GenericMessageToUser, UserJoined, UserLeft}
import org.reactivestreams.Publisher
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{Json, Writes}
import repositories.PlayerRepository

case class PlayerDTO(userId: String, userName: String)

class ConnectedPlayersActor(playerRepository: PlayerRepository) extends Actor {

  var participantsActors: Map[String, ActorRef] = Map.empty[String, ActorRef]
  var playersConnected: Map[String, PlayerDTO] = Map.empty[String, PlayerDTO]
  val logger: Logger = LoggerFactory.getLogger(classOf[ConnectedPlayersActor])

  def broadcast(users: List[PlayerDTO]): Unit = {
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

  def sendMessageToUserId(message: String, userId: String): Unit = {
    participantsActors.get(userId).foreach(_ ! message)
  }

  override def receive: Receive = {
    case UserJoined(userId, actorRef) =>
      logger.info(s"User $userId joined server")
      val foundPlayer = playerRepository.getPlayerById(userId)

      participantsActors += userId -> actorRef
      playersConnected += userId -> PlayerDTO(userId = foundPlayer.userId, userName = foundPlayer.userName)

      broadcast(playersConnected.values.toList)

    case UserLeft(userId) =>
      logger.info(s"User $userId left")
      participantsActors -= userId
      playersConnected -= userId

      broadcast(playersConnected.values.toList)

    case GenericMessageToUser(message, userId) =>
      logger.info(s"sending generic message $message to userId $userId")
      sendMessageToUserId(message, userId)

  }
}

class ConnectedPlayersService(actorSystem: ActorSystem, playerRepository: PlayerRepository)(implicit val mat: Materializer) {
  private[this] val connectedPlayersActor = actorSystem.actorOf(Props(classOf[ConnectedPlayersActor], playerRepository))
  val logger: Logger = LoggerFactory.getLogger(classOf[ConnectedPlayersService])

  def sendMessageToUserId(message: String, userId: String): Unit = connectedPlayersActor ! GenericMessageToUser(message, userId)

  def websocketFlow(userId: String): Flow[Message, Message, Any] = {
    val (actorRef: ActorRef, publisher: Publisher[TextMessage.Strict]) =
      Source.actorRef[String](16, OverflowStrategy.fail)
        .map(msg =>
          TextMessage(msg)
        ).toMat(Sink.asPublisher(false))(Keep.both).run()

    // Announce the user has joined
    connectedPlayersActor ! UserJoined(userId, actorRef)

    val sink: Sink[Message, Any] = Flow[Message]
      .map {
        case TextMessage.Strict(msg) =>
          // incoming message from ws
          logger.info(s"Received: $msg")
        case keepAlive: BinaryMessage =>
          keepAlive.dataStream.runWith(Sink.ignore)
        case _ => logger.error("Not expected message arrived")
      }.to(Sink.onComplete { a =>
      // Announce the user has left
      logger.error(a.get.toString)
      connectedPlayersActor ! UserLeft(userId)
    })
    Flow.fromSinkAndSource(sink, Source.fromPublisher(publisher))
  }
}

