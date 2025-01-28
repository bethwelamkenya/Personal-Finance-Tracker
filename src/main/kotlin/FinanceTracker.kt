package org.example

import java.io.File
import java.time.LocalDate

class FinanceTracker {
    private val transactions = mutableListOf<Transaction>()
    private val transactionsFile = File("transactions.csv") // File to store accounts


    fun addTransaction(account: String, date: LocalDate, type: TransactionType, description: String?, amount: Double) {
        val transaction = Transaction(account, date, type, description, amount)
        transactions.add(transaction)
    }

    fun viewTransactions() {
        if (transactions.isEmpty()) {
            println("No transactions found.")
        } else {
            transactions.sortBy { it.date }
            transactions.forEach { println("${it.date} -- ${it.type} - ${it.account}, ${it.description}: $${it.amount}") }
        }
    }

    fun viewTransactionsByAccount(account: String) {
        val accountTransactions: ArrayList<Transaction> = transactions.filter { it.account == account } as ArrayList<Transaction>
        if (accountTransactions.isEmpty()) {
            println("No transactions found for account $account.")
        } else {
            accountTransactions.sortBy { it.date }
            accountTransactions.forEach { println("${it.date} -- ${it.type} - ${it.description}: $${it.amount}") }
        }
    }

    fun calculateBalance(): Double {
        val incomes = transactions.filter { it.type == TransactionType.Deposit }
        val expenses = transactions.filter { it.type == TransactionType.Withdrawal }
        return incomes.sumOf { it.amount } - expenses.sumOf { it.amount }
    }

    private fun exportToFile(filename: String) {
        if (transactions.isEmpty()) {
            println("No transactions to export.")
            return
        }
        var name: String = filename
        if (!name.endsWith(".csv")) {
            name += ".csv"
        }
        val file = File(name)
        file.writeText("Account,Date,Type,Description,Amount\n")
        transactions.forEach {
            file.appendText("${it.account},${it.date},${it.type},${it.description},${it.amount}\n")
        }
        println("Transactions exported to $name")
    }

    fun saveTransaction(transaction: Transaction) {
        if (!transactionsFile.exists() || transactionsFile.readLines()[0] != "Account,Date,Type,Description,Amount") {
            exportToFile(transactionsFile.name)
        } else {
            transactionsFile.appendText("${transaction.account},${transaction.date},${transaction.type},${transaction.description},${transaction.amount}\n")
            println("Transaction saved")
        }
    }

    fun loadTransactions() {
        transactions.clear()
        if (!transactionsFile.exists() || transactionsFile.readLines()[0] != "Account,Date,Type,Description,Amount") {
            println("No transactions file found. Starting with an empty list.")
            transactionsFile.writeText("Account,Date,Type,Description,Amount")
        }
        transactionsFile.readLines().drop(1).forEach { line ->
            val (account, date, type, description, amount) = line.split(",")
            addTransaction(
                account,
                LocalDate.parse(date),
                TransactionType.valueOf(type),
                description,
                amount.toDouble()
            )
        }
        println("Transactions imported from ${transactionsFile.name}")
    }
}
