package object controllers {
  val DefaultUser: String = "user1@mail.com"
  val TransactionTypeDebit = "DEBIT"
  val TransactionTypeCredit = "CREDIT"
  val TransactionTypes: List[String] = List(TransactionTypeDebit, TransactionTypeCredit)
}
