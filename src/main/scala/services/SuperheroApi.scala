package services

import models.{Attribute, Card}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.BasicResponseHandler

import scala.util.parsing.json.JSON

case class SuperheroApi()  {

  val uri = "https://superheroapi.com/api/"
  val access_token = "103706338543731"

  def get_hero_by_id( identi: Int): String = {
    val get = new HttpGet(uri.concat(access_token).concat("/").concat(identi.toString) )
    val responseget = (new DefaultHttpClient).execute(get)
    val handler = new BasicResponseHandler
    val json = JSON.parseFull(handler.handleResponse(responseget)).get.asInstanceOf[Map[Any, Any]]
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
    //val id: Int = 1
    //val name: String = ""
    //val powerStats: List[Attribute] = []
    //val imageUrl: String = ""
    //Card(id, name, powerStats, imageUrl)
  }
}