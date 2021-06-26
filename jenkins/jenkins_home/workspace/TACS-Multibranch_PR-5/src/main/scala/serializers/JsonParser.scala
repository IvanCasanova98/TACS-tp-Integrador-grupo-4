package serializers

import com.fasterxml.jackson.databind.ObjectMapper

import scala.util.{Failure, Success, Try}

class JsonParser(objectMapper: ObjectMapper) {

  def readEntity[T](entity: String)(implicit dataType: Class[T]): T =
    Try(readJson(entity)) match {
      case Success(deserialized) => deserialized
      case Failure(ex) =>
        throw new RuntimeException(s"An error occurred while trying to deserialize entity ${dataType.getSimpleName}. Exception: ${ex.getMessage}")
    }

  def readEntityNullsEnabled[T](entity: String)(implicit dataType: Class[T]): T =
    Try(readJsonNullEnabled(entity)) match {
      case Success(deserialized) => deserialized
      case Failure(ex) =>
        throw new RuntimeException(s"An error occurred while trying to deserialize entity ${dataType.getSimpleName}. Exception: ${ex.getMessage}")
    }

  def readJson[T](jsonText: String)(implicit dataType: Class[T]): T =
    objectMapper.readValue(jsonText, dataType)

  def writeJson(someObject: Any): String =
    objectMapper.writeValueAsString(someObject)

  def writeJsonNullsEnabled(someObject: Any): String =
    objectMapper.writeValueAsString(someObject)

  def readJsonNullEnabled[T](jsonText: String)(implicit dataType: Class[T]): T =
    objectMapper.readValue(jsonText, dataType)
}
