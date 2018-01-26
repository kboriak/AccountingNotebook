package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject

import models.User
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[H2Profile] {

  private val Users = TableQuery[UsersTable]

  def get(login: String): Future[Option[User]] = db.run(Users.filter(_.login === login).result.headOption)

  def applyDebitTransaction(login: String, amount: BigDecimal): Future[Int] = db.run(
    sqlu"UPDATE USER SET BALANCE = BALANCE + $amount WHERE LOGIN = $login")

  def applyCreditTransaction(login: String, amount: BigDecimal): Future[Int] = db.run(
    sqlu"UPDATE USER SET BALANCE = BALANCE - $amount WHERE LOGIN = $login AND BALANCE >= $amount")

  private class UsersTable(tag: Tag) extends Table[User](tag, "USER") {

    def login = column[String]("LOGIN", O.PrimaryKey)

    def balance = column[BigDecimal]("BALANCE")

    def * = (login, balance) <> (User.tupled, User.unapply)
  }

}
