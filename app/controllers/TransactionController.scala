package controllers

import java.sql.Timestamp

import dao.{TransactionDAO, UserDAO}
import javax.inject._

import models.Transaction
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._

import scala.collection.mutable._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class TransactionController @Inject()(transactionDao: TransactionDAO, userDao: UserDAO, cc: ControllerComponents)(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  implicit val transactionWrite: Writes[Transaction] = (transaction: Transaction) => {
    Json.obj("id" -> transaction.id,
      "userLogin" -> transaction.userLogin,
      "transactionType" -> transaction.transactionType,
      "amount" -> transaction.amount,
      "date" -> transaction.date.getTime,
      "isSuccessful" -> transaction.isSuccessful)
  }

  def newTransaction: Action[AnyContent] = Action.async(request => {
    val errors = ArrayBuffer.empty[String]
    val transactionAmountOpt: Try[Option[BigDecimal]] = Try((request.body.asJson.get \ "amount").asOpt[BigDecimal])
    errors ++= validateTransactionAmount(transactionAmountOpt)
    val transactionTypeOpt = (request.body.asJson.get \ "type").asOpt[String]
    errors ++= validateTransactionType(transactionTypeOpt)
    if (errors.isEmpty) {
      val transactionAmount = transactionAmountOpt.get.get
      val transactionType = transactionTypeOpt.get.map(_.toUpper)
      if (transactionType == TransactionTypeDebit) {
        userDao.applyDebitTransaction(DefaultUser, transactionAmount).map(
          value => finishTransaction(transactionType, transactionAmount, value)
        )
      } else {
        userDao.applyCreditTransaction(DefaultUser, transactionAmount).map(
          value => finishTransaction(transactionType, transactionAmount, value)
        )
      }
    } else {
      Future(BadRequest(Json.obj("errors" -> errors)))
    }
  })

  def finishTransaction(transactionType: String, transactionAmount: BigDecimal, rowsUpdated: Int): Result = {
    val transactionApplied = rowsUpdated == 1
    val transaction = new Transaction(DefaultUser, transactionType, transactionAmount, new Timestamp(DateTime.now().getMillis), transactionApplied)
    transactionDao.add(transaction)
    if (transactionApplied) {
      Ok(Json.obj("data" -> Json.obj("amount" -> transactionAmount, "type" -> transactionType, "status" -> "applied")))
    } else if (!transactionApplied && transactionType == TransactionTypeCredit) {
      UnprocessableEntity(Json.obj("errors" -> List("Insufficient funds for " + transactionAmount + " credit")))
    } else
      InternalServerError(Json.obj("errors" -> List("Request processing failed")))
  }

  def validateTransactionAmount(transactionAmountOpt: Try[Option[BigDecimal]]): ArrayBuffer[String] = {
    val errors = ArrayBuffer.empty[String]
    if (transactionAmountOpt.isFailure) {
      errors += "Transaction amount is not a number"
    }
    if (transactionAmountOpt.isSuccess && transactionAmountOpt.get.isEmpty) {
      errors += "Transaction amount not specified"
    }
    if (transactionAmountOpt.isSuccess && transactionAmountOpt.get.isDefined && transactionAmountOpt.get.get < 0) {
      errors += "Transaction amount is negative"
    }
    errors
  }

  def validateTransactionType(transactionTypeOpt: Option[String]): ArrayBuffer[String] = {
    val errors = ArrayBuffer.empty[String]
    if (transactionTypeOpt.isEmpty) {
      errors += "Transaction type not specified"
    }
    if (transactionTypeOpt.isDefined && !TransactionTypes.contains(transactionTypeOpt.get.map(_.toUpper))) {
      errors += "Unknown transaction type"
    }
    errors
  }

  def transactions: Action[AnyContent] = Action.async(
    transactionDao.get(DefaultUser).map { case (transactions) => Ok(Json.obj("data" -> transactions)) }
  )

}
