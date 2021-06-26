package routes.inputs

object LoginInputs {

  case class LoginInput(name: String, email: String, imageUrl: String, googleId: String, tokenId:String)

}
