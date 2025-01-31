package org.example

import java.security.Key

class BankingTracker {
    private var activeAccount: BankAccount? = null
    var lastActive: Long = System.currentTimeMillis()

    fun updateLastActive() {
        lastActive = System.currentTimeMillis()
    }

    fun createAccount(
        accountNumber: String,
        accountHolder: String,
        bankName: String,
        pin: String,
        dbConnector: DBConnector
    ) {
        val account = BankAccount()
        account.setAccountNumber(accountNumber)
        account.setAccountHolder(accountHolder)
        account.setBankName(bankName)
        account.setPin(pin)
        dbConnector.addBankAccount(account)
        activeAccount = account
    }

    fun logIn(
        accountNumber: String,
        pin: String,
        dbConnector: DBConnector
    ): Boolean {
        val account = dbConnector.getBankAccount(accountNumber)
        if (account == null || pin != account.getPin()) {
            return false
        }
        activeAccount = account
        return true
    }

    fun logOut() {
        activeAccount = null
    }

    fun viewAccount(encryptionHelper: EncryptionHelper, key: Key) {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return
        }
        println("\n=== Account Information ===")
        println("Account Number: ${encryptionHelper.decryptText(activeAccount?.getAccountNumber()!!, key)}")
        println("Account Holder: ${activeAccount?.getAccountHolder()}")
        println("Bank Name: ${activeAccount?.getBankName()}")
        println("Balance: ${activeAccount?.getBalance()} ${activeAccount?.usedCurrency}")
    }

    fun getActiveAccount(): BankAccount? {
        return activeAccount
    }

    fun deposit(
        amount: Double,
        description: String?,
        tracker: FinanceTracker,
        dbConnector: DBConnector
    ): Boolean {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return false
        }
        activeAccount?.deposit(amount, description, tracker, dbConnector)
        dbConnector.updateAccountBalance(
            activeAccount!!.getAccountNumber(),
            activeAccount!!.getBalance()
        )
        println("Deposit successful. New balance: $${activeAccount?.getBalance()}")
        return true
    }

    fun withdraw(
        amount: Double,
        description: String?,
        tracker: FinanceTracker,
        dbConnector: DBConnector,
    ): Boolean {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return false
        }
        if (activeAccount?.getBalance()!! < amount) {
            println("Insufficient funds.")
            return false
        }
        activeAccount?.withdraw(amount, description, tracker, dbConnector)
        dbConnector.updateAccountBalance(
            activeAccount!!.getAccountNumber(),
            activeAccount!!.getBalance()
        )
        println("Withdrawal successful. New balance: $${activeAccount?.getBalance()}")
        return true
    }

    fun transferFunds(receiver: BankAccount, amount: Double, dbConnector: DBConnector, tracker: FinanceTracker, description: String = "Transfer") {
        try {
            activeAccount?.withdraw(amount, "Transfer", tracker, dbConnector)
            receiver.deposit(amount, "Transfer", tracker, dbConnector)
            dbConnector.updateAccountBalance(activeAccount!!.getAccountNumber(), activeAccount!!.getBalance())
            dbConnector.updateAccountBalance(receiver.getAccountNumber(), receiver.getBalance())
            println("Transfer successful. New balance: \$${activeAccount?.getBalance()}")
        } catch (e: Exception) {
            println("Transfer failed. $e")
        }
    }

    fun deleteAccount(tracker: FinanceTracker, dbConnector: DBConnector) {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return
        }
        withdraw(activeAccount!!.getBalance(), "Deleting Account", tracker, dbConnector)
        if (dbConnector.deleteAccount(activeAccount!!.getAccountNumber())) {
            println("Account deleted successfully.")
            logOut()
        } else {
            println("Account deletion failed.")
        }
    }
}