package services
import services.{SuperheroApi}
object borrar {
  def main(args: Array[String]){
      val lista = SuperheroApi().get_hero_by_id(1)
      print(lista)
  }
}
