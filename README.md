Sample app implementing REST API using Scala + Play + Slick + H2 in-memory

This application is a basic money accounting system. It's not doing any real “transactional” work, just emulate the financial transactions logic. The application serves a single user, so we always have just one financial account. No security is implemented.
1. Able to receive credit and debit financial transactions.
2. Serves a single user, so we always have just one financial account.
3. Any transaction, which leads to negative amount in the system are refused with 422 HTTP code.
4. Stores transaction histore in H2 in-memory database.
5. Is used programmatically via its RESTful API.

To run the app download, install and set up JDK 8, sbt, ```clone``` this git repository and simply use
```sbt run```

Available endpoints:

GET   /api/v1/balance 
  Response format:
  ```
  {
    "data": {
        "balance": Decimal
    }
  }
  ```
  
GET   /api/v1/transactions
   Response format:
   ```
   {
     "data": [
        {
          "id": Long,
          "userLogin": String,
          "transactionType": String,
          "amount": Decimal,
          "date": Timestamp in millis,
          "isSuccessful": Boolean
        }
     ]
   }
   ```
 
POST  /api/v1/transaction
  Request format:
  ```
  {
    "amount": Non-negative Decimal,
    "type": String either "debit" or "credit" case-insensitive
  }
  ```
  Response format:
  ```
  {
     "data": {
         "amount": Decimal,
         "type": String,
         "status": String
      }
  }
  ```
  Errors:
  - 400 - Numerous request format violations
  - 422 - Insufficient funds for credit type transaction

TODOs:
- Improve date formatting
- Change the way transaction is applied to user's account to utilize slick's transactionally features
- Imporve HTTP error handling
- Improve app structure
- Improve DB schema
- Add Unit and Integration tests
- Go deeper down the rabbit hole with Scala
