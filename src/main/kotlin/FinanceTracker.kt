package org.example

import java.io.File
import java.security.Key
import java.time.LocalDate

class FinanceTracker {
    fun addTransaction(account: String, date: LocalDate, type: TransactionType, description: String?, amount: Double, dbConnector: DBConnector) {
        val transaction = Transaction(account, date, type, description, amount)
        dbConnector.addTransaction(transaction)
    }

    fun getTransactionsForAccount(account: BankAccount, dbConnector: DBConnector) {
        val accountTransactions: ArrayList<Transaction> =
            dbConnector.getTransactionsForAccount(account.getAccountNumber()) as ArrayList<Transaction>
        if (accountTransactions.isEmpty()) {
            println("No transactions found for account $account.")
        } else {
            accountTransactions.sortBy { it.date }
            accountTransactions.forEach { println("${it.date} -- ${it.type} - ${it.description}: $${it.amount}") }
        }
    }

    fun exportTransactionsForAccount(account: BankAccount, dbConnector: DBConnector, encryptionHelper: EncryptionHelper, key: Key) : String{
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
