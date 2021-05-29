package models

import models.AttributeName.AttributeName

case class Attribute(name: AttributeName, value: Int)

object AttributeName {
  val attributeNames = List(HEIGHT, WEIGHT, INTELLIGENCE, SPEED, POWER, COMBAT, STRENGTH)

  def fromName(name: String): Option[AttributeName] = attributeNames.find(a => a.name() == name)

  trait AttributeName {
    def name(): String
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
}