package serializers

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import org.json4s.Extraction.decompose
import org.json4s.Formats
import org.json4s.jackson.JsonMethods.{compact, parse, render}


trait Json4sSnakeCaseSupport {

  private val jsonStringUnmarshaller =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(ContentTypeRange(ContentTypes.`application/json`))
      .mapWithCharset {
        case (ByteString.empty, _) => throw Unmarshaller.NoContentException
        case (data, charset) => data.decodeString(charset.nioCharset.name)
      }

  protected val jsonStringMarshaller: ToEntityMarshaller[String] =
    Marshaller.stringMarshaller(MediaTypes.`application/json`)

  /**
   * HTTP entity => `A`
   *
   * @tparam A type to decode
   * @return unmarshaller for `A`
   */
  implicit def jsonFromEntityUnmarshaller[A: Manifest](implicit formats: Formats): FromEntityUnmarshaller[A] =
    jsonStringUnmarshaller
      .map(s => parse(s).camelizeKeys.noNulls.extract[A])

  /**
   * `A` => HTTP entity
   *
   * @tparam A type to encode, must be upper bounded by `AnyRef`
   * @return marshaller for any `A` value
   */
  implicit def jsonToEntityMarshaller[A <: AnyRef](implicit formats: Formats): ToEntityMarshaller[A] =
    jsonStringMarshaller.compose(c => compact(render(decompose(c).snakizeKeys)))
}
