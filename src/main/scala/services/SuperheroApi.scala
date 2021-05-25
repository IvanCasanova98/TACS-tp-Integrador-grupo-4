package services

import exceptions.ExceptionsSuperheroApi._
import models.{Attribute, AttributeName, Card}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import scala.util.parsing.json.JSON

case class SuperheroApi() {
  val httpClient: HttpClient = HttpClientBuilder.create.build

  val uri = "https://superheroapi.com/api/"
  val access_token = "103706338543731"

  case class AttributeNameClass(name_new: String) extends AttributeName {
    override def name(): String = name_new
  }

  def get_hero_by_id(hero_id: Int): Card = {
    val get = new HttpGet(uri.concat(access_token).concat("/").concat(hero_id.toString))
    val responseget = httpClient.execute(get)
    val handler = new BasicResponseHandler
    val json = JSON.parseFull(handler.handleResponse(responseget)).get.asInstanceOf[Map[Any, Any]]
    adapt_card_json(json)
  }

  def search_heroes_by_name(hero_name: String): List[Card] = {
    val get = new HttpGet(uri.concat(access_token).concat("/search/").concat(hero_name))
    val responseget = httpClient.execute(get)
    val handler = new BasicResponseHandler
    val json = JSON.parseFull(handler.handleResponse(responseget)).get.asInstanceOf[Map[Any, Any]]("results").asInstanceOf[List[Map[Any, Any]]]
    json.filter(json_has_all_attributes).map(adapt_card_json)

  }

  def adapt_card_json(card_json: Map[Any, Any]): Card = {
    if (json_has_all_attributes(card_json)) {
      val id: Int = card_json("id").asInstanceOf[String].toInt
      val name: String = card_json("name").asInstanceOf[String]
      val imageUrl: String = card_json("image").asInstanceOf[Map[Any, Any]]("url").asInstanceOf[String]
      val powerStats: Map[Any, Any] = card_json("powerstats").asInstanceOf[Map[Any, Any]]
      if (!json_has_all_powerstats(powerStats,card_json("appearance").asInstanceOf[Map[Any,Any]])){throw NotEnoughAttribute()}
      var powerStatsCorrect: List[Attribute] = powerStats.map(power => Attribute(AttributeNameClass(power.asInstanceOf[(String, String)]._1), if (power.asInstanceOf[(String, String)]._2 == "null"){0}else{power.asInstanceOf[(String, String)]._2.toInt})).toList
      val height: String = card_json("appearance").asInstanceOf[Map[Any,Any]]("height").asInstanceOf[List[String]](1).replace(" cm", "")
      val weight: String = card_json("appearance").asInstanceOf[Map[Any,Any]]("weight").asInstanceOf[List[String]](1).replace(" kg", "")
      powerStatsCorrect = powerStatsCorrect ++ List(Attribute(AttributeNameClass("height"), height.toInt), Attribute(AttributeNameClass("weight"), weight.toInt))
      Card(id, name, powerStatsCorrect, imageUrl)}
    else
      {
        if (!card_json.keys.exists(x => x == "error")) {
          println(card_json)
          throw NotEnoughAttribute()
        } else {
          throw UnknownException(card_json("error").toString)
        }

      }
    }

  def json_has_all_attributes(card_json: Map[Any, Any]): Boolean = {
    Set("id", "name", "powerstats", "image").subsetOf(card_json.keys.asInstanceOf[Set[String]])
    }
  def json_has_all_powerstats(powerstarts: Map[Any, Any], appearance: Map[Any, Any]): Boolean = {
    var attribute: List[String] = powerstarts.map(power => power.asInstanceOf[(String, String)]._1).asInstanceOf[List[String]]
    attribute = attribute ++ appearance.keys.toList.asInstanceOf[List[String]]
    Set("combat", "intelligence", "strength", "power", "speed", "height","weight").subsetOf(attribute.toSet)
  }


}