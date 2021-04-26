package services

import routes.inputs.DeckInputs.PostDeckInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DeckService {

  val logger: Logger = LoggerFactory.getLogger(classOf[DeckService])

  def createDeck(deck: PostDeckInput): Int = 2

}
