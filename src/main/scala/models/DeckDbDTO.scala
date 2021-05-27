package models

import scala.collection.immutable.HashMap

case class Card(id: Int, name: String, powerStats: List[Attribute], imageUrl: String){
  def to_json(): HashMap[Any, Any] ={
    val powerStatsJson = powerStats.map(power => HashMap("name"-> power.name.name(), "value"-> power.value))
    HashMap("id"-> id,"name"-> name, "imageUrl" -> imageUrl, "powerStats"-> powerStatsJson)
  }
}

case class DeckDbDTO(id: Int, name: String, cardIds: List[Int])

case class Deck(id: Int, name: String, cards: List[Card])
