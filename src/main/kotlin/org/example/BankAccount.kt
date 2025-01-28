package org.example

import java.time.LocalDate

class BankAccount {
    private var balance: Double = 0.0
    private var accountNumber: String = ""
    private var accountHolder: String = ""
    private var bankName: String = ""
    private var pin: String = ""

    fun deposit(amount: Double, description: String?, tracker: FinanceTracker, dbConnector: DBConnector) {
        balance += amount
        tracker.addTransaction(accountNumber, LocalDate.now(), TransactionType.Deposit, description, amount, dbConnector)
    }

    fun withdraw(amount: Double, description: String?, tracker: FinanceTracker, dbConnector: DBConnector) {
        if (balance >= amount) {
            balance -= amount
            tracker.addTransaction(accountNumber, LocalDate.now(), TransactionType.Withdrawal, description, amount, dbConnector)
        } else {
            println("Insufficient funds.")
        }
    }

    fun getBalance(): Double {
        return balance
    }

    fun getAccountNumber(): String {
        return accountNumber
    }

    fun getAccountHolder(): String {
        return accountHolder
    }

    fun getBankName(): String {
        return bankName
    }

    fun getPin(): String {
        return pin
    }

    fun setAccountNumber(accountNumber: String) {
        this.accountNumber = accountNumber
    }

    fun setAccountHolder(accountHolder: String) {
        this.accountHolder = accountHolder
    }

    fun setBankName(bankName: String) {
        this.bankName = bankName
    }

    fun setPin(pin: String) {
        this.pin = pin
    }

    fun setBalance(balance: Double){
        this.balance = balance
    }


    override fun toString(): String {
        return "$accountNumber,$accountHolder,$bankName,$pin,$balance"
    }

    companion object {
        fun fromString(data: String): BankAccount {
            val parts = data.split(",")
            val account = BankAccount()
            account.setAccountNumber(parts[0])
            account.setAccountHolder(parts[1])
            account.setBankName(parts[2])
            account.setPin(parts[3])
//            account.deposit(parts[4].toDouble(), "")
            account.balance = parts[4].toDouble()
            return account
        }
    }
}
