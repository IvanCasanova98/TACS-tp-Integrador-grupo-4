package models

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
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

class AttributeNameSerializer(val t: Class[AttributeName]) extends StdSerializer[AttributeName](t) {
  override def serialize(attributeName: AttributeName, jgen: JsonGenerator, sp: SerializerProvider): Unit = {
    jgen.writeString(attributeName.name())
  }
}