package models

case class Player(userId: String, userName: String, imageUrl: String, isAdmin: Boolean, isBlocked: Boolean)

case class PlayerScore(userId: String, userName: String, imageUrl: String, score: Int)

case class PlayerPermissions(isAuthenticated: Boolean, isAuthorized: Boolean, isAdmin: Boolean)
