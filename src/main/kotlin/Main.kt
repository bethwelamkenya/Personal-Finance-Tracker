package org.example

import kotlin.system.exitProcess

fun main() {
    val tracker = FinanceTracker()
    val bankingTracker = BankingTracker()
    bankingTracker.loadAccounts()
    tracker.loadTransactions()
    println("Personal Finance Tracker")
    bankingMenu(tracker, bankingTracker);
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

fun bankingMenu(tracker: FinanceTracker, bankingTracker: BankingTracker) {
    while (true) {
        printBankingMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        when (choice) {
            1 -> logInAccount(bankingTracker)
            2 -> viewAccount(bankingTracker)
            3 -> enterTrackerMenu(tracker, bankingTracker)
            4 -> logOut(bankingTracker)
            5 -> addBankAccount(bankingTracker)
            6 -> {
                bankingTracker.saveAccounts()
                exitProcess(0)
            }

            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun logInAccount(bankingTracker: BankingTracker) {
    print("Enter your account number: ")
    val accountNumber = readlnOrNull()
    print("Enter your PIN: ")
    val pin = readlnOrNull()
    if (accountNumber != null && pin != null) {
        if (bankingTracker.logIn(accountNumber, pin)) {
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

fun addBankAccount(bankingTracker: BankingTracker) {
    print("Enter account number: ")
    val accountNumber = readlnOrNull()
    print("Enter account holder: ")
    val accountHolder = readlnOrNull()
    print("Enter bank name: ")
    val bankName = readlnOrNull()
    print("Enter PIN: ")
    val pin = readlnOrNull()
    if (accountNumber != null && accountHolder != null && bankName != null && pin != null) {
        bankingTracker.createAccount(accountNumber, accountHolder, bankName, pin)
        println("Bank account added successfully.")
    } else {
        println("Invalid input. Bank account not added.")
    }
}

fun enterTrackerMenu(tracker: FinanceTracker, bankingTracker: BankingTracker) {
    if (bankingTracker.getActiveAccount() == null) {
        println("No active account. Please log in first.")
        return
    } else {
        trackerMenu(tracker, bankingTracker)
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

fun trackerMenu(tracker: FinanceTracker, bankingTracker: BankingTracker) {
    var run = true
    while (run) {
        printTrackerMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        when (choice) {
            1 -> deposit(tracker, bankingTracker)
            2 -> withdraw(tracker, bankingTracker)
            3 -> viewTransactions(tracker, bankingTracker)
            4 -> run = false
            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun deposit(tracker: FinanceTracker, bankingTracker: BankingTracker) {
    print("Enter the amount to deposit: ")
    val amount = readlnOrNull()?.toDoubleOrNull()
    print("Description: ")
    val description = readlnOrNull()
    if (amount != null) {
        bankingTracker.deposit(amount, description, tracker)
    } else {
        println("Invalid amount. Deposit failed.")
    }
}

fun withdraw(tracker: FinanceTracker, bankingTracker: BankingTracker) {
    print("Enter the amount to withdraw: ")
    val amount = readlnOrNull()?.toDoubleOrNull()
    print("Description: ")
    val description = readlnOrNull()
    if (amount != null) {
        bankingTracker.withdraw(amount, description, tracker)
    } else {
        println("Invalid amount. Withdrawal failed.")
    }
}

fun viewTransactions(tracker: FinanceTracker, bankingTracker: BankingTracker) {
    println("\n=== Transactions ===")
    bankingTracker.getActiveAccount()?.let { tracker.viewTransactionsByAccount(it.getAccountNumber()) }

    println("\n=== Balance ===") //
    println("\nCurrent Balance: $${bankingTracker.getActiveAccount()?.getBalance()}")
}