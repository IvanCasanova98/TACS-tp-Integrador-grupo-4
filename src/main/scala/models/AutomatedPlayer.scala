package models

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, Cancellable}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import javafx.concurrent.Task
import models.Events.Turn
import org.reactivestreams.Publisher
import org.slf4j.{Logger, LoggerFactory}
import routes.Utils.{delayExecution, getRandomItemOfSeq}
import serializers.JsonParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class AutomatedPlayer(matchId: Int)(implicit jsonParser: JsonParser) {
  implicit val system: ActorSystem = ActorSystem("tacs-tp-client")
  val logger: Logger = LoggerFactory.getLogger(classOf[AutomatedPlayer])
  var task: Option[Cancellable] = None

  val (actorRef: ActorRef, publisher: Publisher[TextMessage.Strict]) =
    Source.actorRef[String](16, OverflowStrategy.fail)
      .map(msg =>
        // outgoing message to ws
        TextMessage.Strict(msg)
      ).toMat(Sink.asPublisher(false))(Keep.both).run()

  val sink: Sink[Message, Any] = Flow[Message]
    .map {
      case TextMessage.Strict(msg) =>
        // incoming message from ws
        logger.info(s"Incoming message is $msg")
        if (msg.contains("IN_LOBBY")) {
          actorRef ! "READY:automatedPlayer"
        }
        if (msg.contains("ALL_READY")) {
          actorRef ! "CONNECT GAME"
        }
        if (msg.contains("TURN")) {
          val turnEvent = jsonParser.readJson(msg)(classOf[Turn])
          if (turnEvent.userIdTurn == "automatedPlayer") {
            delayExecution(4)
            val chosenAttribute: String = getRandomItemOfSeq(turnEvent.card.powerStats.filter(p => p.name != null).map(_.name.name()))
            logger.info(s"sending chosen attribute $chosenAttribute")
            actorRef ! s"SET_ATTRIBUTE:$chosenAttribute"
          }
        }
        if(msg.contains("MATCH_RESULT")) {
          task.map(_.cancel())
        }
      case smt => logger.info(s"Something else arrived $smt")
    }.to(Sink.onComplete(_ =>
    logger.info(s"Automated player disconnected from match $matchId")
  ))

  val flow: Flow[Message, TextMessage.Strict, NotUsed] =
  Flow.fromSinkAndSource(sink, Source.fromPublisher(publisher))

  val (upgradeResponse, closed) =
  Http().singleWebSocketRequest(WebSocketRequest(s"ws://localhost:9000/join-match/$matchId?userId=automatedPlayer"), flow)

  val connected: Future[Unit] = upgradeResponse.map { upgrade =>
    if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
      task = Option(sendKeepAlive())
    } else {
      throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
    }
  }
  def sendKeepAlive(): Cancellable =
  system.scheduler.schedule(0.seconds, 30.second, new Runnable {
    override def run(): Unit = {
      actorRef ! ""
    }
  })
}
