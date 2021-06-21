package models

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.{DeserializationContext, JsonNode, SerializerProvider}
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import exceptions.Exceptions.AttributeNotFoundException
import models.AttributeName.{AttributeName, fromName}

case class Attribute(name: AttributeName, value: Int)

object AttributeName {
  val attributeNames = List(HEIGHT, WEIGHT, INTELLIGENCE, SPEED, POWER, COMBAT, STRENGTH)

  def fromName(name: String): AttributeName = attributeNames.find(a => a.name() == name.toUpperCase).getOrElse(throw AttributeNotFoundException(name.toUpperCase()))

  trait AttributeName {
    def name(): String
  }

  object HEIGHT extends AttributeName {
    override def name(): String = "HEIGHT"
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

class AttributeNameDeserializer(val t: Class[AttributeName]) extends StdDeserializer[AttributeName](t) {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): AttributeName = {
    val node: JsonNode = p.getCodec.readTree(p)
    try {
      fromName(node.asText)
    } catch {
      case _: AttributeNotFoundException => null
    }
  }
}