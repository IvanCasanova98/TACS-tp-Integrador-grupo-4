package services

import exceptions.ExceptionsSuperheroApi._
import models.AttributeName.AttributeName
import models.{Attribute, AttributeName, Card}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder

import scala.util.parsing.json.JSON

case class SuperheroApi() {
  val httpClient: HttpClient = HttpClientBuilder.create.build

  var uri = "https://superheroapi.com/api/"
  val access_token = "103706338543731"

  case class AttributeNameClass(name_new: String) extends AttributeName {
    override def name(): String = name_new
  }

  def getHeroById(hero_id: Int): Card = {
    val get = new HttpGet(uri.concat(access_token).concat("/").concat(hero_id.toString))
    val response = httpClient.execute(get)
    val handler = new BasicResponseHandler
    val json = JSON.parseFull(handler.handleResponse(response)).get.asInstanceOf[Map[Any, Any]]
    adaptCardJson(json)
  }

  def searchHeroesByName(hero_name: String): List[Card] = {
    try {
      val get = new HttpGet(uri.concat(access_token).concat("/search/").concat(hero_name))
      val response = httpClient.execute(get)
      val handler = new BasicResponseHandler
      val json = JSON.parseFull(handler.handleResponse(response)).get.asInstanceOf[Map[Any, Any]]("results").asInstanceOf[List[Map[Any, Any]]]
      json.filter(jsonHasAllAttributes).filter(card => jsonHasAllPowerStats(card.asInstanceOf[Map[String, Any]]("powerstats").asInstanceOf[Map[Any, Any]], card.asInstanceOf[Map[String, Any]]("appearance").asInstanceOf[Map[Any, Any]])).map(adaptCardJson)
    } catch {
      case _: Throwable => List.empty
    }
  }

  def adaptCardJson(cardJson: Map[Any, Any]): Card = {
    if (jsonHasAllAttributes(cardJson)) {
      val id: Int = cardJson("id").asInstanceOf[String].toInt
      val name: String = cardJson("name").asInstanceOf[String]
      val imageUrl: String = cardJson("image").asInstanceOf[Map[Any, Any]]("url").asInstanceOf[String]
      val powerStats: Map[Any, Any] = cardJson("powerstats").asInstanceOf[Map[Any, Any]]
      if (!jsonHasAllPowerStats(powerStats, cardJson("appearance").asInstanceOf[Map[Any, Any]])) {
        throw NotEnoughAttributesException()
      }
      var powerStatsCorrect: List[Attribute] = powerStats.map(power => Attribute(AttributeNameClass(power.asInstanceOf[(String, String)]._1), if (power.asInstanceOf[(String, String)]._2 == "null") {
        0
      } else {
        power.asInstanceOf[(String, String)]._2.toInt
      })).toList
      var height: String = cardJson("appearance").asInstanceOf[Map[Any, Any]]("height").asInstanceOf[List[String]](1).replace(" cm", "").replace(",", "")
      var weight: String = cardJson("appearance").asInstanceOf[Map[Any, Any]]("weight").asInstanceOf[List[String]](1).replace(" kg", "").replace(",", "")
      if (height.contains("meters")) {
        height = height.replace(" meters", "")
        val value: Float = height.toFloat * 100
        height = value.toString
      }
      if (weight.contains("tons")) {
        weight = weight.replace(" tons", "")
        val value: Float = weight.toFloat * 1000
        weight = value.toString
      }
      powerStatsCorrect = powerStatsCorrect ++ List(Attribute(AttributeNameClass("height"), height.toFloat.toInt), Attribute(AttributeNameClass("weight"), weight.toFloat.toInt))
      Card(id, name, powerStatsCorrect, imageUrl)
    }
    else {
      if (!cardJson.keys.exists(x => x == "error")) {
        throw NotEnoughAttributesException()
      } else {
        throw UnknownStatusException(cardJson("error").toString)
      }

    }
  }

  def jsonHasAllAttributes(cardJson: Map[Any, Any]): Boolean = {
    Set("id", "name", "powerstats", "image", "appearance").subsetOf(cardJson.keys.asInstanceOf[Set[String]])
  }

  def jsonHasAllPowerStats(powerStats: Map[Any, Any], appearance: Map[Any, Any]): Boolean = {
    var attribute: List[String] = powerStats.map(power => power.asInstanceOf[(String, String)]._1).asInstanceOf[List[String]]
    attribute = attribute ++ appearance.keys.toList.asInstanceOf[List[String]]
    if (Set("height", "weight").subsetOf(attribute.toSet)) {
      if (!appearance("height").asInstanceOf[List[String]].exists(value => value.contains(" cm") || value.contains(" meters"))) {
        return false
      }
      if (!appearance("weight").asInstanceOf[List[String]].exists(value => value.contains(" kg") || value.contains(" tons"))) {
        return false
      }
    }
    Set("combat", "intelligence", "strength", "power", "speed", "height", "weight").subsetOf(attribute.toSet)
  }


}