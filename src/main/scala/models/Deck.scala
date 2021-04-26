package models

import org.json4s.{Formats, DefaultFormats}
import org.json4s.native.Serialization.{write => jWrite}

case class Card(id: Int, name: String, powerStats: List[Attribute], imageUrl: String)

case class Deck(id: Int, name: String, cardIds: List[Int])

object Deck {
  implicit val formats: Formats = DefaultFormats

  def write[T <: AnyRef](value: T): String = jWrite(value)
}
