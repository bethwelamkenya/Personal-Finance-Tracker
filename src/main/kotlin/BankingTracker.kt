package org.example

import java.io.File

class BankingTracker {
    private var activeAccount: BankAccount? = null

    fun createAccount(accountNumber: String, accountHolder: String, bankName: String, pin: String, dbConnector: DBConnector) {
        val account = BankAccount()
        account.setAccountNumber(accountNumber)
        account.setAccountHolder(accountHolder)
        account.setBankName(bankName)
        account.setPin(pin)
        dbConnector.addBankAccount(account)
        activeAccount = account
    }

    fun logIn(accountNumber: String, pin: String, dbConnector: DBConnector): Boolean {
        val account = dbConnector.getBankAccount(accountNumber)
        if (account == null || account.getPin() != pin) {
            return false
        }
        activeAccount = account
        return true
    }

    fun logOut() {
        activeAccount = null
    }

    fun viewAccount() {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return
        }
        println("\n=== Account Information ===")
        println("Account Number: ${activeAccount?.getAccountNumber()}")
        println("Account Holder: ${activeAccount?.getAccountHolder()}")
        println("Bank Name: ${activeAccount?.getBankName()}")
        println("Balance: $${activeAccount?.getBalance()}")
    }

    fun getActiveAccount(): BankAccount? {
        return activeAccount
    }

    fun deposit(amount: Double, description: String?, tracker: FinanceTracker, dbConnector: DBConnector) {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return
        }
        activeAccount?.deposit(amount, description, tracker, dbConnector)
        dbConnector.updateAccountBalance(activeAccount!!.getAccountNumber(), activeAccount!!.getBalance())
        println("Deposit successful. New balance: $${activeAccount?.getBalance()}")
    }

    fun withdraw(amount: Double, description: String?, tracker: FinanceTracker, dbConnector: DBConnector) {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return
        }
        if (activeAccount?.getBalance()!! < amount) {
            println("Insufficient funds.")
            return
        }
        activeAccount?.withdraw(amount, description, tracker, dbConnector)
        dbConnector.updateAccountBalance(activeAccount!!.getAccountNumber(), activeAccount!!.getBalance())
        println("Withdrawal successful. New balance: $${activeAccount?.getBalance()}")
    }
}