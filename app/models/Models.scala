package models

import java.sql.Timestamp

case class User(login: String, balance: BigDecimal)

case class Transaction(id: Long, userLogin: String, transactionType: String, amount: BigDecimal, date: Timestamp, isSuccessful: Boolean) {
  def this(userLogin: String, transactionType: String, amount: BigDecimal, date: Timestamp, isSuccessful: Boolean) = this(0, userLogin, transactionType, amount, date, isSuccessful)
}
