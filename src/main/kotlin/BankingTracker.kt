package org.example

import java.io.File

class BankingTracker {
    private val accounts = mutableListOf<BankAccount>()
    private var activeAccount: BankAccount? = null
    private val accountsFile = File("accounts.txt") // File to store accounts

    fun loadAccounts() {
        if (!accountsFile.exists()) {
            println("No accounts file found. Starting with an empty list.")
            return
        }

        accounts.clear()
        accountsFile.readLines().forEach { line ->
            val account = BankAccount.fromString(line)
            accounts.add(account)
        }
        println("Accounts loaded successfully.")
    }

    fun saveAccounts() {
        val accountData = accounts.joinToString("\n") { it.toString() }
        accountsFile.writeText(accountData)
        println("Accounts saved successfully.")
    }

    fun createAccount(accountNumber: String, accountHolder: String, bankName: String, pin: String) {
        val account = BankAccount()
        account.setAccountNumber(accountNumber)
        account.setAccountHolder(accountHolder)
        account.setBankName(bankName)
        account.setPin(pin)
        accounts.add(account)
        activeAccount = account
    }

    fun logIn(accountNumber: String, pin: String): Boolean {
        val account = accounts.find { it.getAccountNumber() == accountNumber }
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

    fun deposit(amount: Double, description: String?, tracker: FinanceTracker) {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return
        }
        activeAccount?.deposit(amount, description, tracker)
        println("Deposit successful. New balance: $${activeAccount?.getBalance()}")
    }

    fun withdraw(amount: Double, description: String?, tracker: FinanceTracker) {
        if (activeAccount == null) {
            println("No active account. Please log in first.")
            return
        }
        if (activeAccount?.getBalance()!! < amount) {
            println("Insufficient funds.")
            return
        }
        activeAccount?.withdraw(amount, description, tracker)
        println("Withdrawal successful. New balance: $${activeAccount?.getBalance()}")
    }
}