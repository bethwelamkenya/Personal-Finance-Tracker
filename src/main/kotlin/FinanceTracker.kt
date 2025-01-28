package org.example

import java.io.File
import java.time.LocalDate

class FinanceTracker {
    fun addTransaction(account: String, date: LocalDate, type: TransactionType, description: String?, amount: Double, dbConnector: DBConnector) {
        val transaction = Transaction(account, date, type, description, amount)
        dbConnector.addTransaction(transaction)
    }

    fun getTransactionsForAccount(account: String, dbConnector: DBConnector) {
        val accountTransactions: ArrayList<Transaction> =
            dbConnector.getTransactionsForAccount(account) as ArrayList<Transaction>
        if (accountTransactions.isEmpty()) {
            println("No transactions found for account $account.")
        } else {
            accountTransactions.sortBy { it.date }
            accountTransactions.forEach { println("${it.date} -- ${it.type} - ${it.description}: $${it.amount}") }
        }
    }
}
