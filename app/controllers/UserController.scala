package controllers

import dao.UserDAO
import javax.inject._

import models.User
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(userDao: UserDAO, cc: ControllerComponents)(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  implicit val userBalanceWriter: Writes[User] = (user: User) => {
    Json.obj("balance" -> user.balance)
  }

  def balance: Action[AnyContent] = Action.async(
    userDao.get(DefaultUser).map { case (user) => Ok(Json.obj("data" -> user)) }
  )
}
