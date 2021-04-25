package models


case class Card(id: Int, name: String, powerStats: List[Attribute], imageUrl: String)

case class Deck(id: Int, name: String, cardIds: List[Int])
