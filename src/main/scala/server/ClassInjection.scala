package server

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, PropertyNamingStrategy, SerializationFeature}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import models.AttributeName.AttributeName
import models.MatchStatus.{FINISHED, PAUSED}
import models._
import repositories.daos.{DeckLocalDao, MatchLocalDAO, MovementLocalDAO}
import repositories.dbdtos.MatchDBDTO
import repositories.{DeckRepository, MatchRepository, MovementRepository, PlayerRepository}
import serializers.JsonParser
import services.{DeckService, LoginService, MatchService, SuperheroApi}

import scala.collection.mutable

trait ClassInjection {

  val deckLocalDb: mutable.HashMap[Int, DeckDbDTO] = mutable.HashMap[Int, DeckDbDTO]()
  deckLocalDb.put(1, DeckDbDTO(1, "Primer mazo", List(10, 11, 12, 13)))
  deckLocalDb.put(2, DeckDbDTO(2, "Batman super mazo", List(1, 8, 4, 5, 3)))
  deckLocalDb.put(3, DeckDbDTO(3, "Mazo 3", List(1, 2, 3, 4)))
  deckLocalDb.put(4, DeckDbDTO(4, "Another deck", List(1, 2, 4, 16)))
  deckLocalDb.put(5, DeckDbDTO(5, "A-bomb mazo", List(1, 2, 3, 4)))
  deckLocalDb.put(6, DeckDbDTO(6, "Batman super deck", List(1, 4, 5, 3, 2)))

  val matchLocalDb: mutable.HashMap[Int, MatchDBDTO] = mutable.HashMap[Int, MatchDBDTO](
    1 -> MatchDBDTO(1, FINISHED, "104065320855221322833", "104725077753706905086", 3, Option("104065320855221322833")),
    2 -> MatchDBDTO(2, PAUSED, "102400486230688279463", "104065320855221322833", 1, None)
  )

  val playerDb: mutable.HashMap[String, Player] = mutable.HashMap[String, Player](
    ("104725077753706905086" -> Player("104725077753706905086", "Franco Giannotti", "https://lh3.googleusercontent.com/a-/AOh14GgjwE38QY3xY6yljKclSoVRnByF-59pAG1wdvx_=s96-c", isAdmin = true, isBlocked = false)),
    ("104065320855221322833" -> Player("104065320855221322833", "Julieta Abuin", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c", isAdmin = true, isBlocked = false)),
    ("102400486230688279463" -> Player("102400486230688279463", "FRANCO GIANNOTTI CALENS", "https://lh3.googleusercontent.com/a-/AOh14GgjwE38QY3xY6yljKclSoVRnByF-59pAG1wdvx_=s96-c", isAdmin = true, isBlocked = false)),
      ("107032331312948829616" -> Player("107032331312948829616", "Chiara M", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c", isAdmin = true, isBlocked = false)),
      ("115748028387079548757" -> Player("115748028387079548757", "Ivan C", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c", isAdmin = true, isBlocked = false)),
        ("107090515790711287955" -> Player("107090515790711287955", "Julieta Lucia Abuin", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c", isAdmin = true, isBlocked = false))
  )

  val movementDb: mutable.HashMap[Int, List[Movement]] = mutable.HashMap[Int, List[Movement]](
    1 -> List(Movement(0, "STRENGTH", 2, 4, "104065320855221322833","104065320855221322833"), Movement(1, "INTELLIGENCE", 1, 3, "104065320855221322833","104725077753706905086"))
  )

  val superheroApi: SuperheroApi = SuperheroApi()

  private def defaultObjectMapper(): ObjectMapper = {
    val customModule = new SimpleModule("CustomModule")
    .addSerializer(classOf[AttributeName], new AttributeNameSerializer(classOf[AttributeName]))

    new ObjectMapper()
      .registerModule(DefaultScalaModule)
      .registerModule(new Jdk8Module)
      .registerModule(new JavaTimeModule)
      .registerModule(customModule)
      .disable(SerializationFeature.INDENT_OUTPUT)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
      .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
  }

  val jsonParser = new JsonParser(defaultObjectMapper())

  //Local Dao for saving stuff in memory
  val deckDao = new DeckLocalDao(deckLocalDb)
  val matchDao = new MatchLocalDAO(matchLocalDb)
  val movementDao = new MovementLocalDAO(movementDb)

  val deckRepository = new DeckRepository(deckDao)
  val matchRepository = new MatchRepository(matchDao)
  val playerRepository = new PlayerRepository(playerDb)
  val movementRepository = new MovementRepository(movementDao)

  val deckService = new DeckService(deckRepository, superheroApi)
  val matchService = new MatchService(matchRepository, playerRepository, deckService, movementRepository)
  val loginService = new LoginService(playerRepository)

}
