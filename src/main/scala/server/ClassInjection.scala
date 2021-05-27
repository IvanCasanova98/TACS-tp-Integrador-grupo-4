package server

import akka.actor.ActorSystem
import akka.stream.Materializer
import models.{DeckDbDTO, Match, Player}
import repositories.daos.{DeckLocalDao, MatchLocalDAO}
import repositories.dbdtos.MatchDBDTO
import repositories.{DeckRepository, MatchRepository, PlayerRepository}
import services.{DeckService, LoginService, MatchService, SuperheroApi}

import scala.collection.mutable

trait ClassInjection {

  val deckLocalDb: mutable.HashMap[Int, DeckDbDTO] = mutable.HashMap[Int, DeckDbDTO]()
  deckLocalDb.put(1, DeckDbDTO(1, "Primer mazo", List(1,2,3,4)))
  deckLocalDb.put(2, DeckDbDTO(2, "Batman super mazo", List(1,2,4,5,3,2,2)))
  deckLocalDb.put(3, DeckDbDTO(3, "Mazo 3", List(1,2,3,4)))
  deckLocalDb.put(4, DeckDbDTO(4, "Another deck", List(1,2,4,5,3,2,2)))
  deckLocalDb.put(5, DeckDbDTO(5, "A-bomb mazo", List(1,2,3,4)))
  deckLocalDb.put(6, DeckDbDTO(6, "Batman super deck", List(1,2,4,5,3,2,2)))

  val matchLocalDb: mutable.HashMap[Int, MatchDBDTO] = mutable.HashMap[Int, MatchDBDTO]()

  val playerDb: mutable.HashMap[String, Player] = mutable.HashMap[String, Player](
    ("104725077753706905086"->Player("104725077753706905086","Franco Giannotti", "", isAdmin = true,isBlocked = false)),
    ("104065320855221322833" -> Player("104065320855221322833", "Julieta Abuin", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c",isAdmin = true, isBlocked = false)),
      ("102400486230688279463" -> Player("102400486230688279463", "FRANCO GIANNOTTI CALENS", "", isAdmin = true, isBlocked = false))

  )

  val superheroApi: SuperheroApi = SuperheroApi()

  //Local Dao for saving stuff in memory
  val deckDao = new DeckLocalDao(deckLocalDb)
  val matchDao = new MatchLocalDAO(matchLocalDb)

  val deckRepository = new DeckRepository(deckDao)
  val matchRepository = new MatchRepository(matchDao)
  val playerRepository = new PlayerRepository(playerDb)

  val deckService = new DeckService(deckRepository, superheroApi)
  val matchService = new MatchService(matchRepository, playerRepository, deckService)
  val loginService = new LoginService(playerRepository)

}
