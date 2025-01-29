package org.example

import java.time.LocalDate

class SavingsTracker {
    fun createSavingsGoal(bankingTracker: BankingTracker, goalName: String, targetAmount: Double, dbConnector: DBConnector){
        if (dbConnector.addSavingsGoal(bankingTracker.getActiveAccount()!!.getAccountNumber(), goalName, targetAmount)) {
            println("Savings goal added successfully.")
        } else {
            println("Failed to add savings goal.")
        }
    }

    fun viewSavingsGoals(bankingTracker: BankingTracker, dbConnector: DBConnector) {
        val accountNumber = bankingTracker.getActiveAccount()!!.getAccountNumber()
        val savingsGoals = dbConnector.getSavingsGoals(accountNumber)

        if (savingsGoals.isEmpty()) {
            println("No savings goals found.")
            return
        }

        println("\n=== Your Savings Goals ===")
        for (goal in savingsGoals) {
            println("ID: ${goal.id}, Goal: ${goal.goalName}, Target: ${goal.targetAmount}, Saved: ${goal.savedAmount}")
        }
    }

    fun depositIntoSavings(
        bankingTracker: BankingTracker,
        goalId: Int,
        amount: Double,
        tracker: FinanceTracker,
        dbConnector: DBConnector
    ) {
        val account = bankingTracker.getActiveAccount()!!
        if (dbConnector.depositToSavings(account.getAccountNumber(), goalId, amount)) {
            bankingTracker.withdraw(
                amount,
                "Savings Deposit",
                tracker,
                dbConnector
            )
            println("Deposit successful!")
        } else {
            println("Deposit failed. Check goal ID or amount.")
        }
    }

    fun withdrawFromSavings(bankingTracker: BankingTracker, goalId: Int, amount: Double, tracker: FinanceTracker, dbConnector: DBConnector) {
        val account = bankingTracker.getActiveAccount()!!
        if (dbConnector.withdrawFromSavings(account.getAccountNumber(), goalId, amount)) {
            bankingTracker.deposit(
                amount,
                "Savings Withdrawal",
                tracker,
                dbConnector
            )
            println("Withdrawal successful!")
        } else {
            println("Withdrawal failed. Check goal ID or amount.")
        }
    }
}