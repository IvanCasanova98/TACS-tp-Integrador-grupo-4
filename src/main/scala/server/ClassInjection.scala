package server

import akka.actor.ActorSystem
import akka.stream.Materializer
import models.{Deck, Match, Player}
import repositories.daos.{DeckLocalDao, MatchLocalDAO}
import repositories.dbdtos.MatchDBDTO
import repositories.{DeckRepository, MatchRepository, PlayerRepository}
import services.{ConnectedPlayersService, DeckService, LoginService, MatchService}

import scala.collection.mutable

trait ClassInjection {

  val deckLocalDb: mutable.HashMap[Int, Deck] = mutable.HashMap[Int, Deck]()
  deckLocalDb.put(1, Deck(1, "Primer mazo", List(1,2,3,4)))
  deckLocalDb.put(2, Deck(2, "Batman super mazo", List(1,2,4,5,3,2,2)))
  deckLocalDb.put(3, Deck(3, "Mazo 3", List(1,2,3,4)))
  deckLocalDb.put(4, Deck(4, "Another deck", List(1,2,4,5,3,2,2)))
  deckLocalDb.put(5, Deck(5, "A-bomb mazo", List(1,2,3,4)))
  deckLocalDb.put(6, Deck(6, "Batman super deck", List(1,2,4,5,3,2,2)))

  val matchLocalDb: mutable.HashMap[Int, MatchDBDTO] = mutable.HashMap[Int, MatchDBDTO]()

  val playerDb: mutable.HashMap[String, Player] = mutable.HashMap[String, Player](
    ("104725077753706905086"->Player("104725077753706905086","Franco Giannotti",isAdmin = true,isBlocked = false)),
    ("104065320855221322833" -> Player("104065320855221322833", "Julieta Abuin", isAdmin = true, isBlocked = false)),
      ("102400486230688279463" -> Player("102400486230688279463", "FRANCO GIANNOTTI CALENS", isAdmin = true, isBlocked = false))

  )


  //Local Dao for saving stuff in memory
  val deckDao = new DeckLocalDao(deckLocalDb)
  val matchDao = new MatchLocalDAO(matchLocalDb)

  val deckRepository = new DeckRepository(deckDao)
  val matchRepository = new MatchRepository(matchDao)
  val playerRepository = new PlayerRepository(playerDb)

  val deckService = new DeckService(deckRepository)
  val matchService = new MatchService(matchRepository)
  val loginService = new LoginService(playerRepository)

}
