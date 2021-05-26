package server

import akka.actor.ActorSystem
import akka.stream.Materializer
import models.{Deck, Match, Player}
import repositories.daos.{DeckLocalDao, MatchLocalDAO}
import repositories.dbdtos.MatchDBDTO
import repositories.{DeckRepository, MatchRepository, PlayerRepository}
import services.{DeckService, LoginService, MatchService}

import scala.collection.mutable

trait ClassInjection {

  val deckLocalDb: mutable.HashMap[Int, Deck] = mutable.HashMap[Int, Deck]()
  deckLocalDb.put(1, Deck(1, "Primer mazo", List(1,2,3,4)))
  deckLocalDb.put(2, Deck(2, "Batman super mazo", List(1,2,4,5,3,2,2)))
  deckLocalDb.put(3, Deck(3, "Mazo 3", List(1,2,3,4)))
  deckLocalDb.put(4, Deck(4, "Another deck", List(1,2,4,5,3,2,2)))
  deckLocalDb.put(5, Deck(5, "A-bomb mazo", List(1,2,3,4)))
  deckLocalDb.put(6, Deck(6, "Batman super deck", List(1,2,4,5,3,2,2)))

  val matchLocalDb: mutable.HashMap[Int, MatchDBDTO] = mutable.HashMap[Int, MatchDBDTO]()

  val playerDb: mutable.HashMap[String, Player] = mutable.HashMap[String, Player](
    ("104725077753706905086"->Player("104725077753706905086","Franco Giannotti", "", isAdmin = true,isBlocked = false)),
    ("104065320855221322833" -> Player("104065320855221322833", "Julieta Abuin", "https://lh3.googleusercontent.com/a-/AOh14Gh5tYvnhd0arKFn9ot7FU6D6mrnSpfuh6_hAPvMsg=s96-c,104065320855221322833,eyJhbGciOiJSUzI1NiIsImtpZCI6ImNkNDliMmFiMTZlMWU5YTQ5NmM4MjM5ZGFjMGRhZGQwOWQ0NDMwMTIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiMTA1ODQ5NDk2Mzc1My1kcmF1cXVmMDZ0c3Uxam5ibDdrMTNwdHJwOThzMzIzZC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjEwNTg0OTQ5NjM3NTMtZHJhdXF1ZjA2dHN1MWpuYmw3azEzcHRycDk4czMyM2QuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDQwNjUzMjA4NTUyMjEzMjI4MzMiLCJlbWFpbCI6Imp1bGlldGFidWluQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoidXRfUHEzWDYwVEwtNTRFSmdkMG9SZyIsIm5hbWUiOiJKdWxpZXRhIEFidWluIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdoNXRZdm5oZDBhcktGbjlvdDdGVTZENm1yblNwZnVoNl9oQVB2TXNnPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Ikp1bGlldGEiLCJmYW1pbHlfbmFtZSI6IkFidWluIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2MjE3NDI3OTksImV4cCI6MTYyMTc0NjM5OSwianRpIjoiYjk5OWI1OGE3ZWJkZjM4ZjA0NTM2MmFiNTdmNTIyM2NlY2MyMDNkMyJ9.WloKtoQo3qV_7qnWxlzRFzzgC1HOeMgeHmsK4-r1aBw-2tE8cJJNkzJAWvsgRQGC0a6AqBU5pCDHYs1YK_h59SC_AxTrgLMC6B9chSalcDX5lTotiW-b3E1YV_IeIE8bGk_8OP_QaWiNjNMCAsDSRcd4xhX0O8JhsJgY6IZRGYNBbK2nZXB0GfSA4742szbn-TVEQ1W_MejV_xwLQcaD1fPZukhZ2lW9-ZBhY2SbQ-KhqWZY618sp_ScSGSwqSHinMCxBjqFFrnn_AFklcRP77uFP7FYV_rDgdEzJi7gNj3ygFJlBTZ1zR3z72XtVm0Cy1jxG6LIw1J6T2xneB6L1w",isAdmin = true, isBlocked = false)),
      ("102400486230688279463" -> Player("102400486230688279463", "FRANCO GIANNOTTI CALENS", "", isAdmin = true, isBlocked = false))

  )


  //Local Dao for saving stuff in memory
  val deckDao = new DeckLocalDao(deckLocalDb)
  val matchDao = new MatchLocalDAO(matchLocalDb)

  val deckRepository = new DeckRepository(deckDao)
  val matchRepository = new MatchRepository(matchDao)
  val playerRepository = new PlayerRepository(playerDb)

  val deckService = new DeckService(deckRepository)
  val matchService = new MatchService(matchRepository)
  val loginService = new LoginService(playerRepository)

}
