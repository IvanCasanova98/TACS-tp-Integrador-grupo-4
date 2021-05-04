package models

case class Attribute(name: AttributeName, value: Int)

trait AttributeName {
  def name(): String

  val attributeNames = List(HEIGHT, WEIGHT, INTELLIGENCE, SPEED, POWER, COMBAT, STRENGTH)

  def fromName(name: String): Option[AttributeName] = attributeNames.find(a => a.name() == name)

}

object HEIGHT extends AttributeName {
  override def name(): String = "POWER"
}

object WEIGHT extends AttributeName {
  override def name(): String = "WEIGHT"
}

object INTELLIGENCE extends AttributeName {
  override def name(): String = "INTELLIGENCE"
}

object SPEED extends AttributeName {
  override def name(): String = "SPEED"
}

object POWER extends AttributeName {
  override def name(): String = "POWER"
}

object COMBAT extends AttributeName {
  override def name(): String = "COMBAT"
}

object STRENGTH extends AttributeName {
  override def name(): String = "STRENGTH"
}
