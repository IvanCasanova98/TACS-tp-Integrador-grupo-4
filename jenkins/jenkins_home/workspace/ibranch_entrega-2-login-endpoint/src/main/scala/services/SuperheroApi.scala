package services

import exceptions.ExceptionsSuperheroApi._
import models.{Attribute, AttributeName, Card}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.BasicResponseHandler

import scala.util.parsing.json.JSON

case class SuperheroApi() {

  val uri = "https://superheroapi.com/api/"
  val access_token = "103706338543731"

  case class AtributeNameClass(name_new: String) extends AttributeName {
    override def name(): String = name_new
  }

  def get_hero_by_id(hero_id: Int): Card = {
    val get = new HttpGet(uri.concat(access_token).concat("/").concat(hero_id.toString))
    val responseget = (new DefaultHttpClient).execute(get)
    val handler = new BasicResponseHandler
    val json = JSON.parseFull(handler.handleResponse(responseget)).get.asInstanceOf[Map[Any, Any]]
    adapt_card_json(json)
  }

  def search_heroes_by_name(hero_name: String): List[Card] = {
    val get = new HttpGet(uri.concat(access_token).concat("/search/").concat(hero_name))
    val responseget = (new DefaultHttpClient).execute(get)
    val handler = new BasicResponseHandler
    val json = JSON.parseFull(handler.handleResponse(responseget)).get.asInstanceOf[Map[Any, Any]]("results").asInstanceOf[List[Map[Any, Any]]]
    json.filter(json_has_all_atributes).map(adapt_card_json)

  }

  def adapt_card_json(card_json: Map[Any, Any]): Card = {
    if (json_has_all_atributes(card_json)) {
      val id: Int = card_json("id").asInstanceOf[String].toInt
      val name: String = card_json("name").asInstanceOf[String]
      val imageUrl: String = card_json("image").asInstanceOf[Map[Any, Any]]("url").asInstanceOf[String]
      val powerStats: Map[Any, Any] = card_json("powerstats").asInstanceOf[Map[Any, Any]]
      val powerStatsCorrect: List[Attribute] = powerStats.map(power => Attribute(AtributeNameClass(power.asInstanceOf[(String, String)]._1), power.asInstanceOf[(String, String)]._2.toInt)).toList
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

  def json_has_all_atributes(card_json: Map[Any, Any]): Boolean = {
    Set("id", "name", "powerstats", "image").subsetOf(card_json.keys.asInstanceOf[Set[String]])
    }


}