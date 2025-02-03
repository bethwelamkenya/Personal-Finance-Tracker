package org.example

import kotlin.system.exitProcess

val tracker = FinanceTracker()
val bankingTracker = BankingTracker()
val dbConnector = DBConnector()
val savingsTracker = SavingsTracker()
val encryptionHelper = EncryptionHelper()

// 1. Generate a new AES key (you can store it securely in your app)
val key = encryptionHelper.getKeyFromString(dbConnector.getSecretKey(1)!!)

fun main() {
    println("Personal Finance Tracker")
    startCLI()

}

fun parseCommand(input: String): Pair<String, List<String>> {
    val parts = input.trim().split("\\s+".toRegex())
    return if (parts.isEmpty()) {
        Pair("", emptyList())
    } else {
        Pair(parts[0].lowercase(), parts.drop(1))
    }
}

fun startCLI() {
    var isRunning = true
    val inactivityLimit = 5 * 60 * 1000 // 5 minutes

    println("Welcome to the Personal Finance Tracker CLI!")
    println("Type 'help' for a list of commands.")

    while (isRunning) {
        if (System.currentTimeMillis() - bankingTracker.lastActive > inactivityLimit) {
            println("Session expired due to inactivity.")
            bankingTracker.logOut()
        }
        print("> ")
        val input = readlnOrNull() ?: ""
        val (command, args) = parseCommand(input)

        when (command) {
            "login" -> handleLogin(args)
            "deposit" -> handleDeposit(args)
            "withdraw" -> handleWithdraw(args)
            "deposit-savings" -> handleDepositSavings(args)
            "withdraw-savings" -> handleWithdrawSavings(args)
            "view-transactions" -> handleViewTransactions()
            "export-transactions" -> handleExportTransactions()
            "transfer" -> handleTransfer(args)
            "view-account" -> handleViewAccount()
            "change-pin" -> handleChangePin()
            "create-savings-goal" -> handleCreateSavingsGoal(args)
            "view-savings-goals" -> handleViewSavingsGoals()
            "create-account" -> handleCreateAccount()
            "delete-account" -> handleDeleteAccount()
            "logout" -> handleLogout()
            "exit" -> {
                println("Exiting...")
                isRunning = false
            }

            "help" -> printHelp()
            "use-menu" -> handleUseMenu()
            else -> println("Unknown command. Type 'help' for a list of commands.")
        }
    }
}

fun printHelp() {
    println(
        """
        Available Commands:
        - login <accountNumber> <pin>                     : Log in to your account
        - deposit <amount> [description]                  : Deposit money
        - withdraw <amount> [description]                 : Withdraw money
        - deposit-savings <amount> <goalId> [description] : Deposit money into savings
        - withdraw-savings <amount> <goalId> [description]: Withdraw money from savings
        - view-transactions                               : View transaction history
        - export-transactions                             : Export Transactions history
        - transfer <amount> <toAccount>                   : Transfer money to another account
        - view-account                                    : View current account details
        - change-pin                                      : Change pin of the current account
        - logout                                          : Log out of the current account
        - create-savings-goal <goalName> <targetAmount>   : Log out of the current account
        - view-savings-goals                              : Log out of the current account
        - create-account                                  : Create an account
        - delete-account                                  : Delete the current account
        - exit                                            : Exit the application
        - help                                            : Show this help message
        - use-menu                                        : Use Menu-Based CLI
    """.trimIndent()
    )
}

fun handleLogin(args: List<String>) {
    if (args.size != 2) {
        println("Usage: login <accountNumber> <pin>")
        return
    }
    val accountNumber = args[0]
    val pin = args[1]
    val loggedIn = bankingTracker.logIn(
        encryptionHelper.encryptText(accountNumber, key),
        encryptionHelper.encryptText(pin, key),
        dbConnector
    )
    if (loggedIn) {
        println(
            "Logged in to account: ${
                encryptionHelper.decryptText(
                    bankingTracker.getActiveAccount()!!.getAccountNumber(), key
                )
            }"
        )
    } else {
        println("Login failed.")
    }
}

fun handleDeposit(args: List<String>) {
    if (bankingTracker.getActiveAccount() == null) {
        println("Please log in first.")
        return
    }
    if (args.isEmpty()) {
        println("Usage: deposit <amount> [description]")
        return
    }
    val amount = args[0].toDoubleOrNull()
    if (amount == null || amount <= 0) {
        println("Invalid amount.")
        return
    }
    val description = args.getOrNull(1) ?: "Deposit"
    bankingTracker.deposit(amount, description, tracker, dbConnector)
    println("Deposited $$amount. New balance: $${bankingTracker.getActiveAccount()!!.getBalance()}")
}

fun handleWithdraw(args: List<String>) {
    if (bankingTracker.getActiveAccount() == null) {
        println("Please log in first.")
        return
    }
    if (args.isEmpty()) {
        println("Usage: withdraw <amount> [description]")
        return
    }
    val amount = args[0].toDoubleOrNull()
    if (amount == null || amount <= 0) {
        println("Invalid amount.")
        return
    }
    val description = args.getOrNull(1) ?: "Withdraw"
    bankingTracker.withdraw(amount, description, tracker, dbConnector)
    println("Withdrew $$amount. New balance: $${bankingTracker.getActiveAccount()!!.getBalance()}")
}

fun handleDepositSavings(args: List<String>) {
    if (bankingTracker.getActiveAccount() == null) {
        println("Please log in first.")
        return
    }
    if (args.size < 2) {
        println("Usage: deposit-savings <amount> <goalId> [description]")
        return
    }
    val amount = args[0].toDoubleOrNull()
    if (amount == null || amount <= 0) {
        println("Invalid amount.")
        return
    }
    val goalId = args[0].toIntOrNull()
    if (goalId == null) {
        println("Invalid goal Id.")
        return
    }
    val description = args.getOrNull(2) ?: "Deposit Savings"
    savingsTracker.depositIntoSavings(bankingTracker, goalId, amount, tracker, dbConnector, description)
    println(
        "Deposited $$amount into saving account. New bank balance: $${
            bankingTracker.getActiveAccount()!!.getBalance()
        }"
    )
}

fun handleWithdrawSavings(args: List<String>) {
    if (bankingTracker.getActiveAccount() == null) {
        println("Please log in first.")
        return
    }
    if (args.size < 2) {
        println("Usage: withdraw-savings <amount> <goalId> [description]")
        return
    }
    val amount = args[0].toDoubleOrNull()
    if (amount == null || amount <= 0) {
        println("Invalid amount.")
        return
    }
    val goalId = args[0].toIntOrNull()
    if (goalId == null) {
        println("Invalid goal Id.")
        return
    }
    val description = args.getOrNull(2) ?: "Deposit Savings"
    savingsTracker.withdrawFromSavings(bankingTracker, goalId, amount, tracker, dbConnector, description)
    println(
        "Withdrew $$amount from saving account. New bank balance: $${
            bankingTracker.getActiveAccount()!!.getBalance()
        }"
    )
}

fun handleViewTransactions() {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }

    val transactions = tracker.viewTransactionsForAccount(account, dbConnector)

    println("\n=== Transactions ===")
    tracker.viewTransactionsForAccount(account, dbConnector)

    println("\n=== Balance ===")
    val balance = account.getBalance()
    println("| Current Balance: ${String.format("%.2f", balance).padStart(15)} |")
    println("|----------------------------------|")
}

fun handleLogout() {
    bankingTracker.logOut()
    println("Logged out.")
}

fun handleTransfer(args: List<String>) {
    if (bankingTracker.getActiveAccount() == null) {
        println("Please log in first.")
        return
    }
    if (args.size < 2) {
        println("Usage: transfer <amount> <toAccount>")
        return
    }

    val amount = args[0].toDoubleOrNull()
    if (amount == null || amount <= 0) {
        println("Invalid amount.")
        return
    }

    val toAccount = args[1]
    val recipientAccount = dbConnector.getBankAccount(encryptionHelper.encryptText(toAccount, key))

    if (recipientAccount == null) {
        println("Recipient account not found.")
        return
    }

    if (bankingTracker.getActiveAccount()!!.getBalance() < amount) {
        println("Insufficient balance.")
        return
    }
    bankingTracker.transferFunds(recipientAccount, amount, dbConnector, tracker)
}

fun handleViewAccount() {
    bankingTracker.viewAccount(encryptionHelper, key)
}

fun handleChangePin() {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }

    print("Enter current PIN: ")
    val currentPin = readlnOrNull()
    if (!bankingTracker.logIn(
            account.getAccountNumber(),
            encryptionHelper.encryptText(currentPin!!, key),
            dbConnector
        )
    ) {
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

fun handleCreateAccount() {
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

fun handleExportTransactions() {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }
    tracker.exportTransactionsForAccount(account, dbConnector, encryptionHelper, key)
}

fun handleDeleteAccount() {
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

fun handleCreateSavingsGoal(args: List<String>) {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }

    if (args.size < 2) {
        println("Usage: create-savings-goal <goalName> <targetAmount>")
        return
    }
    val targetAmount = args[1].toDoubleOrNull()
    if (targetAmount == null || targetAmount <= 0) {
        println("Invalid amount.")
        return
    }
    savingsTracker.createSavingsGoal(bankingTracker, args[0], targetAmount, dbConnector)
}

fun handleViewSavingsGoals() {
    val account = bankingTracker.getActiveAccount()
    if (account == null) {
        println("No active account. Please log in first.")
        return
    }
    savingsTracker.viewSavingsGoals(bankingTracker, dbConnector)
}

fun printBankingMenu() {
    println("\nWelcome to the Banking Application!")
    println("1. Log In to Account") // done, done
    println("2. View Account") //, done
    println("3. Transact Money") // done, done
    println("4. Savings Account") // done, done
    println("5. Change Pin") // done, done
    println("6. Log Out") // done, done
    println("7. Create Bank Account") // done, done
    println("8. Delete Bank Account") // done, done
    println("9. Use Command-Based CLI") // done, done
    println("10. Exit") // done, done
    println("Enter your choice:")
}

fun handleUseMenu() {
    while (true) {
        printBankingMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        bankingTracker.updateLastActive()
        when (choice) {
            1 -> logInAccount()
            2 -> handleViewAccount()
            3 -> {
                if (bankingTracker.getActiveAccount() == null) {
                    println("No active account. Please log in first.")
                } else {
                    trackerMenu()
                }
            }

            4 -> {
                if (bankingTracker.getActiveAccount() == null) {
                    println("No active account. Please log in first.")
                } else {
                    savingsMenu()
                }
            }

            5 -> handleChangePin()
            6 -> handleLogout()
            7 -> handleCreateAccount()
            8 -> handleDeleteAccount()
            9 -> startCLI()
            10 -> exitProcess(0)
            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun logInAccount() {
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

fun isStrongPin(pin: String): Boolean {
    return pin.length >= 4 && pin.any { it.isDigit() }
}

fun printTrackerMenu() {
    println("\nWelcome to the Personal Finance Tracker!")
    println("1. Deposit") // done, done
    println("2. Withdraw") // done, done
    println("3. Transfer Money") // done, done
    println("4. View Transactions") // done, done
    println("5. Export Transactions") // done, done
    println("6. Back")
    println("Enter your choice:")
}

fun trackerMenu() {
    var run = true
    while (run) {
        printTrackerMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        when (choice) {
            1 -> deposit()
            2 -> withdraw()
            3 -> transferMoney()
            4 -> handleViewTransactions()
            5 -> handleExportTransactions()
            6 -> run = false
            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun deposit() {
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

fun withdraw() {
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

fun transferMoney() {
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

fun printSavingsMenu() {
    println("\nWelcome to the Savings Menu!")
    println("1. Create Savings Goal") // done, done
    println("2. View Savings Goals") // done, done
    println("3. Deposit into Savings") // done, done
    println("4. Withdraw from Savings") // done, done
    println("5. Back") // done, done
    println("Enter your choice:")
}

fun savingsMenu() {
    var run = true
    while (run) {
        printSavingsMenu()
        val choice = readlnOrNull()?.toIntOrNull()
        when (choice) {
            1 -> createSavingsGoal()
            2 -> handleViewSavingsGoals()
            3 -> depositIntoSavings()
            4 -> withdrawFromSavings()
            5 -> run = false
            else -> println("Invalid choice. Please try again.")
        }
    }
}

fun createSavingsGoal() {
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

fun depositIntoSavings() {
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

fun withdrawFromSavings() {
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
