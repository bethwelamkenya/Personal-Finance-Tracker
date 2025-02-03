package org.example

class SavingsTracker {
    fun createSavingsGoal(
        bankingTracker: BankingTracker,
        goalName: String,
        targetAmount: Double,
        dbConnector: DBConnector
    ) {
        if (dbConnector.addSavingsGoal(
                bankingTracker.getActiveAccount()!!.getAccountNumber(),
                goalName,
                targetAmount
            )
        ) {
            println("Savings goal added successfully.")
        } else {
            println("Failed to add savings goal.")
        }
    }

    fun viewSavingsGoals(bankingTracker: BankingTracker, dbConnector: DBConnector) {
        val accountNumber = bankingTracker.getActiveAccount()!!.getAccountNumber()
        val savingsGoals = dbConnector.getSavingsGoals(accountNumber)

        if (savingsGoals.isEmpty()) {
            println("| No savings goals found. Add one to get started!     |")
            println("|-----------------------------------------------------|")
            return
        }

        println("\n=== Your Savings Goals ===")
        println("| ID  | Goal                | Target     | Saved     |")
        println("|-----|---------------------|------------|-----------|")

        for (goal in savingsGoals) {
            val formattedRow = String.format(
                "| %-4d| %-20s| %10.2f | %9.2f |",
                goal.id,
                goal.goalName.take(20),  // Truncate long names
                goal.targetAmount,
                goal.savedAmount
            )
            println(formattedRow)
        }
    }

    fun depositIntoSavings(
        bankingTracker: BankingTracker,
        goalId: Int,
        amount: Double,
        tracker: FinanceTracker,
        dbConnector: DBConnector,
        description: String = "Savings Deposit"
    ) {
        val account = bankingTracker.getActiveAccount()!!
        if (account.getBalance() >= amount) {
            if (dbConnector.depositToSavings(account.getAccountNumber(), goalId, amount)) {
                bankingTracker.withdraw(
                    amount,
                    description,
                    tracker,
                    dbConnector
                )
                println("Deposit successful!")
            } else {
                println("Deposit failed. Check goal ID or amount.")
            }
        } else {
            println("Deposit failed. Check amount.")
        }
    }

    fun withdrawFromSavings(
        bankingTracker: BankingTracker,
        goalId: Int,
        amount: Double,
        tracker: FinanceTracker,
        dbConnector: DBConnector,
        description: String = "Savings Withdrawal"
    ) {
        val account = bankingTracker.getActiveAccount()!!
        if (dbConnector.withdrawFromSavings(account.getAccountNumber(), goalId, amount)) {
            bankingTracker.deposit(
                amount,
                description,
                tracker,
                dbConnector
            )
            println("Withdrawal successful!")
        } else {
            println("Withdrawal failed. Check goal ID or amount.")
        }
    }
}