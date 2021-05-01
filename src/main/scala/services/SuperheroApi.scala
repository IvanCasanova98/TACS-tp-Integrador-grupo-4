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
    if (Set("id", "name", "powerstats", "image").subsetOf(json.keys.asInstanceOf[Set[String]])) {
      val id: Int = json("id").asInstanceOf[String].toInt
      val name: String = json("name").asInstanceOf[String]
      val imageUrl: String = json("image").asInstanceOf[Map[Any, Any]]("url").asInstanceOf[String]
      val powerStats: Map[Any, Any] = json("powerstats").asInstanceOf[Map[Any, Any]]
      val powerStatsCorrect: List[Attribute] = powerStats.map(power => Attribute(AtributeNameClass(power.asInstanceOf[(String, String)]._1), power.asInstanceOf[(String, String)]._2.toInt)).toList
      Card(id, name, powerStatsCorrect, imageUrl)
    }
    else {
      if (!json.keys.exists(x => x == "error")) {
        println(json)
        throw NotEnoughAttribute()
      } else {
        throw UnknownException(json("error").toString)
      }

    }
  }

  def search_heroes_by_name(hero_name: String): String = {
    val get = new HttpGet(uri.concat(access_token).concat("/search/").concat(hero_name) )
    val responseget = (new DefaultHttpClient).execute(get)
    val handler = new BasicResponseHandler
    val json = JSON.parseFull(handler.handleResponse(responseget)).get.asInstanceOf[List[Map[Any, Any]]]
    if (Set("id","name","powerstats","image").subsetOf(json.keys.asInstanceOf[Set[String]])){
      val id: Int = json.get("id").get.asInstanceOf[String].toInt
      val name: String = json.get("name").get.asInstanceOf[String]
      val imageUrl: String = json.get("image").get.asInstanceOf[Map[Any, Any]].get("url").get.asInstanceOf[String]
      val powerStats: List[Attribute] = json.get("powerstats").get.asInstanceOf[List[Map[Any, Any]]].foreach(power => Attribute(AttributeName(power.asInstanceOf[Map[Any, Any]].get("name")), power.asInstanceOf[Map[Any, Any]].get("value").asInstanceOf[Int]))
      //Card(id, name, json.get("powerstats").asInstanceOf[List[Attribute]], imageUrl).toString
      json.toString()
    }
    else{
      Set(json.keys).contains("id").toString()
    }
  }
}