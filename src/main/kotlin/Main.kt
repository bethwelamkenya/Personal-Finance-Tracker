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
    println("4. Change Pin")
    println("5. Log Out")
    println("6. Add Bank Account")
    println("7. Delete Bank Account")
    println("8. Exit")
    println("Enter your choice:")
}

fun bankingMenu(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    val inactivityLimit = 5 * 60 * 1000 // 5 minutes
    while (true) {
        if (System.currentTimeMillis() - bankingTracker.lastActive > inactivityLimit) {
            println("Session expired due to inactivity.")
            bankingTracker.logOut()
            return
        }

        printBankingMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        bankingTracker.updateLastActive()
        when (choice) {
            1 -> logInAccount(bankingTracker, dbConnector)
            2 -> viewAccount(bankingTracker)
            3 -> enterTrackerMenu(tracker, bankingTracker, dbConnector)
            4 -> changePin(bankingTracker, dbConnector)
            5 -> logOut(bankingTracker)
            6 -> addBankAccount(bankingTracker, dbConnector)
            7 -> deleteBankAccount(tracker, bankingTracker, dbConnector)
            8 -> exitProcess(0)

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

fun changePin(bankingTracker: BankingTracker, dbConnector: DBConnector) {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }

    print("Enter current PIN: ")
    val currentPin = readlnOrNull()
    if (currentPin != account.getPin()) {
        println("Incorrect PIN. PIN change failed.")
        return
    }

    print("Enter new PIN: ")
    val newPin = readlnOrNull()
    if (newPin != null && newPin.length >= 4) {
        if (dbConnector.updatePin(account.getAccountNumber(), newPin)) {
            println("PIN updated successfully.")
        } else {
            println("PIN update failed.")
        }
    } else {
        println("Invalid PIN. Must be at least 4 digits.")
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

fun deleteBankAccount(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }

    print("Are you sure you want to delete your account? (yes/no): ")
    val confirm = readlnOrNull()
    if (confirm.equals("yes", ignoreCase = true)) {
        bankingTracker.deleteAccount(tracker, dbConnector)
    } else {
        println("Account deletion canceled.")
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
    println("3. Transfer Money")
    println("4. View Transactions")
    println("5. Export Transactions")
    println("6. Back")
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
            3 -> transferMoney(tracker, bankingTracker, dbConnector)
            4 -> viewTransactions(tracker, bankingTracker, dbConnector)
            5 -> exportTransactions(tracker, bankingTracker, dbConnector)
            6 -> run = false
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

fun transferMoney(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    val senderAccount = bankingTracker.getActiveAccount()
    if (senderAccount == null) {
        println("No active account. Please log in first.")
        return
    }

    print("Enter recipient account number: ")
    val recipientAccountNumber = readlnOrNull()
    val recipientAccount = dbConnector.getBankAccount(recipientAccountNumber!!)

    if (recipientAccount == null) {
        println("Recipient account not found.")
        return
    }

    print("Enter transfer amount: ")
    val amount = readlnOrNull()?.toDoubleOrNull()

    if (amount == null || amount <= 0 || senderAccount.getBalance() < amount) {
        println("Invalid amount or insufficient balance.")
        return
    }
    bankingTracker.transferFunds(recipientAccount, amount, dbConnector, tracker)
}

fun viewTransactions(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    println("\n=== Transactions ===")
    bankingTracker.getActiveAccount()?.let { tracker.getTransactionsForAccount(it, dbConnector) }

    println("\n=== Balance ===") //
    println("\nCurrent Balance: $${bankingTracker.getActiveAccount()?.getBalance()}")
}

fun exportTransactions(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector) {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }
    tracker.exportTransactionsForAccount(account, dbConnector)
}
