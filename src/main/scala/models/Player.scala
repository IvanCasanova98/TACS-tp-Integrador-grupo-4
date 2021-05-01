package models

case class Player(userId: String, userName: String, isAdmin: Boolean, isBlocked: Boolean)

case class PlayerPermissions(isAuthenticated: Boolean, isAuthorized: Boolean, isAdmin: Boolean)
