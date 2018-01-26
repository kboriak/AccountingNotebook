package dao

import java.sql.Timestamp
import javax.inject.Inject

import models.Transaction
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class TransactionDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[H2Profile] {

  private val Transactions = TableQuery[TransactionsTable]

  def get(userLogin: String): Future[Seq[Transaction]] = db.run(Transactions.filter(_.userLogin === userLogin).result)

  def add(transaction: Transaction): Future[Int] = db.run(Transactions += transaction)

  private class TransactionsTable(tag: Tag) extends Table[Transaction](tag, "TRANSACTION") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def userLogin = column[String]("USER_LOGIN")
    def transactionType = column[String]("TRANSACTION_TYPE")
    def amount = column[BigDecimal]("AMOUNT")
    def date = column[Timestamp]("DATE")
    def isSuccessful = column[Boolean]("IS_SUCCESSFUL")

    def * = (id, userLogin, transactionType, amount, date, isSuccessful) <> (Transaction.tupled, Transaction.unapply)
  }

}
