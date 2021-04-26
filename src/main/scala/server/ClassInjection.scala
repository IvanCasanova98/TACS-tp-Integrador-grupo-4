package server

import services.DeckService

trait ClassInjection {

  val deckService = new DeckService()

}
