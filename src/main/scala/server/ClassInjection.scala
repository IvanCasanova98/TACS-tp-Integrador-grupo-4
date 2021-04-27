package server

import models.Deck
import repositories.DeckRepository
import services.DeckService
import scala.collection.mutable

trait ClassInjection {

  val deckDb: mutable.HashMap[Int, Deck] = mutable.HashMap[Int, Deck]()
  val deckRepository = new DeckRepository(deckDb)
  val deckService = new DeckService(deckRepository)

}
