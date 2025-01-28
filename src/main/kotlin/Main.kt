package org.example

import kotlin.system.exitProcess

fun main() {
    val tracker = FinanceTracker()
    val bankingTracker = BankingTracker()
    val dbConnector = DBConnector()
    println("Personal Finance Tracker")
    bankingMenu(tracker, bankingTracker, dbConnector)
}

fun printBankingMenu() {
    println("\nWelcome to the Banking Application!")
    println("1. Log In to Account")
    println("2. View Account")
    println("3. Transact Money")
    println("4. Log Out")
    println("5. Add Bank Account")
    println("6. Exit")
    println("Enter your choice:")
}

fun bankingMenu(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    while (true) {
        printBankingMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        when (choice) {
            1 -> logInAccount(bankingTracker, dbConnector)
            2 -> viewAccount(bankingTracker)
            3 -> enterTrackerMenu(tracker, bankingTracker, dbConnector)
            4 -> logOut(bankingTracker)
            5 -> addBankAccount(bankingTracker, dbConnector)
            6 -> exitProcess(0)

            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun logInAccount(bankingTracker: BankingTracker, dbConnector: DBConnector) {
    print("Enter your account number: ")
    val accountNumber = readlnOrNull()
    print("Enter your PIN: ")
    val pin = readlnOrNull()
    if (accountNumber != null && pin != null) {
        if (bankingTracker.logIn(accountNumber, pin, dbConnector)) {
            println("Login successful.")
        } else {
            println("Invalid account number or PIN.")
        }
    } else {
        println("Invalid input. Please try again.")
    }
}

fun viewAccount(bankingTracker: BankingTracker) {
    bankingTracker.viewAccount()
}

fun addBankAccount(bankingTracker: BankingTracker, dbConnector: DBConnector) {
    print("Enter account number: ")
    val accountNumber = readlnOrNull()
    print("Enter account holder: ")
    val accountHolder = readlnOrNull()
    print("Enter bank name: ")
    val bankName = readlnOrNull()
    print("Enter PIN: ")
    val pin = readlnOrNull()
    if (accountNumber != null && accountHolder != null && bankName != null && pin != null) {
        bankingTracker.createAccount(accountNumber, accountHolder, bankName, pin, dbConnector)
        println("Bank account added successfully.")
    } else {
        println("Invalid input. Bank account not added.")
    }
}

fun enterTrackerMenu(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    if (bankingTracker.getActiveAccount() == null) {
        println("No active account. Please log in first.")
        return
    } else {
        trackerMenu(tracker, bankingTracker, dbConnector)
    }
}

fun logOut(bankingTracker: BankingTracker) {
    bankingTracker.logOut()
    println("Logged out.")
}

fun printTrackerMenu() {
    println("\nWelcome to the Personal Finance Tracker!")
    println("1. Deposit")
    println("2. Withdraw")
    println("3. View Transactions")
    println("4. Back")
    println("Enter your choice:")
}

fun trackerMenu(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    var run = true
    while (run) {
        printTrackerMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        when (choice) {
            1 -> deposit(tracker, bankingTracker, dbConnector)
            2 -> withdraw(tracker, bankingTracker, dbConnector)
            3 -> viewTransactions(tracker, bankingTracker, dbConnector)
            4 -> run = false
            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun deposit(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    print("Enter the amount to deposit: ")
    val amount = readlnOrNull()?.toDoubleOrNull()
    print("Description: ")
    val description = readlnOrNull()
    if (amount != null) {
        bankingTracker.deposit(amount, description, tracker, dbConnector)
    } else {
        println("Invalid amount. Deposit failed.")
    }
}

fun withdraw(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    print("Enter the amount to withdraw: ")
    val amount = readlnOrNull()?.toDoubleOrNull()
    print("Description: ")
    val description = readlnOrNull()
    if (amount != null) {
        bankingTracker.withdraw(amount, description, tracker, dbConnector)
    } else {
        println("Invalid amount. Withdrawal failed.")
    }
}

fun viewTransactions(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    println("\n=== Transactions ===")
    bankingTracker.getActiveAccount()?.let { tracker.getTransactionsForAccount(it.getAccountNumber(), dbConnector) }

    println("\n=== Balance ===") //
    println("\nCurrent Balance: $${bankingTracker.getActiveAccount()?.getBalance()}")
}