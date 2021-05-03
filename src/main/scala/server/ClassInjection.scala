package server

import models.{Deck, Player}
import repositories.daos.DeckLocalDao
import repositories.{DeckRepository, PlayerRepository}
import services.{DeckService, LoginService}

import scala.collection.mutable

trait ClassInjection {

  val deckLocalDb: mutable.HashMap[Int, Deck] = mutable.HashMap[Int, Deck]()
  deckLocalDb.put(1, Deck(1, "myDeck", List(1,2,3,4)))
  //Local Dao for saving stuff in memory
  val deckDao = new DeckLocalDao(deckLocalDb)

  val deckRepository = new DeckRepository(deckDao)
  val deckService = new DeckService(deckRepository)


  val playerDb: mutable.HashMap[String, Player] = mutable.HashMap[String, Player](
    ("104725077753706905086"->Player("IDIDID","Franco Giannotti",true,false))
  )
  val playerRepository = new PlayerRepository(playerDb)
  val loginService = new LoginService(playerRepository)

}
