package org.example

import java.io.File
import java.security.Key
import java.time.LocalDate

class FinanceTracker {
    fun addTransaction(
        account: String,
        date: LocalDate,
        type: TransactionType,
        description: String?,
        amount: Double,
        dbConnector: DBConnector
    ) {
        val transaction = Transaction(account, date, type, description, amount)
        dbConnector.addTransaction(transaction)
    }

    fun viewTransactionsForAccount(account: BankAccount, dbConnector: DBConnector) {
        val transactions: ArrayList<Transaction> =
            dbConnector.getTransactionsForAccount(account.getAccountNumber()) as ArrayList<Transaction>
        if (transactions.isEmpty()) {
            println("| No transactions found.                                     |")
        } else {
            println("| Date       | Description              | Amount       |")
            println("|------------|--------------------------|--------------|")

            transactions.forEach { transaction ->
                val formattedDate = transaction.date.toString().take(10)  // Adjust date format as needed
                val formattedDesc = transaction.description!!.take(25).padEnd(25)
                val formattedAmount = String.format("%12.2f", transaction.amount)

                println("| $formattedDate | $formattedDesc | $ $formattedAmount |")
            }
        }
    }

    fun exportTransactionsForAccount(
        account: BankAccount,
        dbConnector: DBConnector,
        encryptionHelper: EncryptionHelper,
        key: Key
    ): String {
        val transactions = dbConnector.getTransactionsForAccount(account.getAccountNumber()) as ArrayList<Transaction>
        transactions.sortBy { it.date }
        val fileName = "${encryptionHelper.decryptText(account.getAccountNumber(), key)}_transactions.csv"

        File(fileName).bufferedWriter().use { writer ->
            writer.write("Date,Type,Description,Amount\n")
            for (transaction in transactions) {
                writer.write("${transaction.date},${transaction.type},${transaction.description},${transaction.amount}\n")
            }
        }
        println("Transactions exported to $fileName")
        return File(fileName).absolutePath
    }

}
