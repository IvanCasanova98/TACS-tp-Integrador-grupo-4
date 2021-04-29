package server

import models.{Deck, Player}
import repositories.{DeckRepository, PlayerRepository}
import services.{DeckService, LoginService}

import scala.collection.mutable

trait ClassInjection {

  val deckDb: mutable.HashMap[Int, Deck] = mutable.HashMap[Int, Deck]()
  val deckRepository = new DeckRepository(deckDb)
  val deckService = new DeckService(deckRepository)


  val playerDb: mutable.HashMap[String, Player] = mutable.HashMap[String, Player]()
  val playerRepository = new PlayerRepository(playerDb)
  val loginService = new LoginService(playerRepository)

}
