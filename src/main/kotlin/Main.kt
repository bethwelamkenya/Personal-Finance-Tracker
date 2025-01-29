package org.example

import java.security.Key
import kotlin.system.exitProcess

fun main() {
    val tracker = FinanceTracker()
    val bankingTracker = BankingTracker()
    val dbConnector = DBConnector()
    val savingsTracker = SavingsTracker()
    val encryptionHelper = EncryptionHelper()

    // 1. Generate a new AES key (you can store it securely in your app)
    val key = encryptionHelper.getKeyFromString(dbConnector.getSecretKey(1)!!)
    println("Personal Finance Tracker")
    bankingMenu(tracker, bankingTracker, dbConnector, savingsTracker, encryptionHelper, key)
}

fun printBankingMenu() {
    println("\nWelcome to the Banking Application!")
    println("1. Log In to Account")
    println("2. View Account")
    println("3. Transact Money")
    println("4. Savings Account")
    println("5. Change Pin")
    println("6. Log Out")
    println("7. Add Bank Account")
    println("8. Delete Bank Account")
    println("9. Exit")
    println("Enter your choice:")
}

fun bankingMenu(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    savingsTracker: SavingsTracker,
    encryptionHelper: EncryptionHelper,
    key: Key
) {
    val inactivityLimit = 5 * 60 * 1000 // 5 minutes
    while (true) {
        if (System.currentTimeMillis() - bankingTracker.lastActive > inactivityLimit) {
            println("Session expired due to inactivity.")
            bankingTracker.logOut()
//            return
        }

        printBankingMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        bankingTracker.updateLastActive()
        when (choice) {
            1 -> logInAccount(bankingTracker, dbConnector, encryptionHelper, key)
            2 -> viewAccount(bankingTracker, encryptionHelper, key)
            3 -> enterTrackerMenu(tracker, bankingTracker, dbConnector, encryptionHelper, key)
            4 -> enterSavingsMenu(tracker, bankingTracker, dbConnector, savingsTracker)
            5 -> changePin(bankingTracker, dbConnector, encryptionHelper, key)
            6 -> logOut(bankingTracker)
            7 -> addBankAccount(bankingTracker, dbConnector, encryptionHelper, key)
            8 -> deleteBankAccount(tracker, bankingTracker, dbConnector)
            9 -> exitProcess(0)

            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun logInAccount(
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    encryptionHelper: EncryptionHelper,
    key: Key
) {
    print("Enter your account number: ")
    val accountNumber = readlnOrNull()
    print("Enter your PIN: ")
    val pin = readlnOrNull()
    if (accountNumber != null && pin != null) {
        if (bankingTracker.logIn(
                encryptionHelper.encryptText(accountNumber, key),
                encryptionHelper.encryptText(pin, key),
                dbConnector
            )
        ) {
            println("Login successful.")
        } else {
            println("Invalid account number or PIN.")
        }
    } else {
        println("Invalid input. Please try again.")
    }
}

fun changePin(bankingTracker: BankingTracker, dbConnector: DBConnector, encryptionHelper: EncryptionHelper, key: Key) {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }

    print("Enter current PIN: ")
    val currentPin = readlnOrNull()
    if (!bankingTracker.logIn(account.getAccountNumber(), encryptionHelper.encryptText(currentPin!!, key), dbConnector)) {
        println("Incorrect PIN. PIN change failed.")
        return
    }
    var run = true
    while (run) {
        print("Enter new PIN: ")
        val newPin = readlnOrNull()
        if (newPin != null) {
            if (isStrongPin(newPin)) {
                if (dbConnector.updatePin(account.getAccountNumber(), encryptionHelper.encryptText(newPin, key))) {
                    println("PIN updated successfully.")
                } else {
                    println("PIN update failed.")
                }
                run = false
            } else {
                println("Invalid PIN. Must be at least 4 digits.")
            }
        } else {
            println("Invalid PIN. Please try again")
        }
    }
}

fun viewAccount(bankingTracker: BankingTracker, encryptionHelper: EncryptionHelper, key: Key) {
    bankingTracker.viewAccount(encryptionHelper, key)
}

fun isStrongPin(pin: String): Boolean {
    return pin.length >= 4 && pin.any { it.isDigit() }
}

fun addBankAccount(
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    encryptionHelper: EncryptionHelper,
    key: Key
) {
    var run = true
    var running = true
    while (running) {
        print("Enter account number: ")
        val accountNumber = readlnOrNull()
        if (accountNumber != null && dbConnector.getBankAccount(accountNumber) == null) {
            print("Enter account holder: ")
            val accountHolder = readlnOrNull()
            print("Enter bank name: ")
            val bankName = readlnOrNull()
            while (run) {
                print("Enter PIN: ")
                val pin = readlnOrNull()
                if (accountHolder != null && bankName != null && pin != null) {
                    if (isStrongPin(pin)) {
                        bankingTracker.createAccount(
                            encryptionHelper.encryptText(accountNumber, key),
                            accountHolder,
                            bankName,
                            encryptionHelper.encryptText(pin, key),
                            dbConnector
                        )
                        println("Bank account added successfully.")
                        run = false
                    } else {
                        println("Invalid PIN. Must be at least 4 digits.")
                    }
                } else {
                    println("Invalid input. Bank account not added.")
                    run = false
                }
            }
            running = false
        } else {
            println("Account number already exists. Please try again.")
        }
    }
}

fun deleteBankAccount(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector
) {
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

fun enterTrackerMenu(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    encryptionHelper: EncryptionHelper,
    key: Key
) {
    if (bankingTracker.getActiveAccount() == null) {
        println("No active account. Please log in first.")
        return
    } else {
        trackerMenu(tracker, bankingTracker, dbConnector, encryptionHelper, key)
    }
}

fun enterSavingsMenu(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    savingsTracker: SavingsTracker
) {
    if (bankingTracker.getActiveAccount() == null) {
        println("No active account. Please log in first.")
        return
    } else {
        savingsMenu(tracker, bankingTracker, dbConnector, savingsTracker)
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

fun trackerMenu(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    encryptionHelper: EncryptionHelper,
    key: Key
) {
    var run = true
    while (run) {
        printTrackerMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        when (choice) {
            1 -> deposit(tracker, bankingTracker, dbConnector)
            2 -> withdraw(tracker, bankingTracker, dbConnector)
            3 -> transferMoney(tracker, bankingTracker, dbConnector, encryptionHelper, key)
            4 -> viewTransactions(tracker, bankingTracker, dbConnector)
            5 -> exportTransactions(tracker, bankingTracker, dbConnector, encryptionHelper, key)
            6 -> run = false
            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun deposit(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector
) {
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

fun withdraw(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector
) {
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

fun transferMoney(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    encryptionHelper: EncryptionHelper,
    key: Key
) {
    val senderAccount = bankingTracker.getActiveAccount()
    if (senderAccount == null) {
        println("No active account. Please log in first.")
        return
    }

    print("Enter recipient account number: ")
    val recipientAccountNumber = readlnOrNull()
    val recipientAccount = dbConnector.getBankAccount(encryptionHelper.encryptText(recipientAccountNumber!!, key))

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

fun exportTransactions(tracker: FinanceTracker, bankingTracker: BankingTracker, dbConnector: DBConnector, encryptionHelper: EncryptionHelper, key: Key) {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }
    tracker.exportTransactionsForAccount(account, dbConnector, encryptionHelper, key)
}

fun printSavingsMenu() {
    println("\nWelcome to the Savings Menu!")
    println("1. Create Savings Goal")
    println("2. View Savings Goals")
    println("3. Deposit into Savings")
    println("4. Withdraw from Savings")
    println("5. Back")
    println("Enter your choice:")
}

fun savingsMenu(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    savingsTracker: SavingsTracker
) {
    var run = true
    while (run) {
        printSavingsMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        when (choice) {
            1 -> createSavingsGoal(bankingTracker, dbConnector, savingsTracker)
            2 -> viewSavingsGoals(bankingTracker, dbConnector, savingsTracker)
            3 -> depositIntoSavings(tracker, bankingTracker, dbConnector, savingsTracker)
            4 -> withdrawFromSavings(tracker, bankingTracker, dbConnector, savingsTracker)
            5 -> run = false
            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun createSavingsGoal(bankingTracker: BankingTracker, dbConnector: DBConnector, savingsTracker: SavingsTracker) {
    print("Enter savings goal name: ")
    val goalName = readlnOrNull()
    print("Enter target amount: ")
    val targetAmount = readlnOrNull()?.toDoubleOrNull()

    if (goalName != null && targetAmount != null) {
        savingsTracker.createSavingsGoal(bankingTracker, goalName, targetAmount, dbConnector)
    } else {
        println("Invalid input. Please try again.")
    }
}

fun viewSavingsGoals(bankingTracker: BankingTracker, dbConnector: DBConnector, savingsTracker: SavingsTracker) {
    savingsTracker.viewSavingsGoals(bankingTracker, dbConnector)
}

fun depositIntoSavings(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    savingsTracker: SavingsTracker
) {
    print("Enter savings goal ID: ")
    val goalId = readlnOrNull()?.toIntOrNull()
    print("Enter amount to deposit: ")
    val amount = readlnOrNull()?.toDoubleOrNull()

    if (goalId != null && amount != null) {
        savingsTracker.depositIntoSavings(bankingTracker, goalId, amount, tracker, dbConnector)
    } else {
        println("Invalid input. Please try again.")
    }
}

fun withdrawFromSavings(
    tracker: FinanceTracker,
    bankingTracker: BankingTracker,
    dbConnector: DBConnector,
    savingsTracker: SavingsTracker
) {
    print("Enter savings goal ID: ")
    val goalId = readlnOrNull()?.toIntOrNull()
    print("Enter amount to withdraw: ")
    val amount = readlnOrNull()?.toDoubleOrNull()

    if (goalId != null && amount != null) {
        savingsTracker.withdrawFromSavings(bankingTracker, goalId, amount, tracker, dbConnector)
    } else {
        println("Invalid input. Please try again.")
    }
}
