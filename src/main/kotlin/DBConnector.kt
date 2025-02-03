package org.example

class DBConnector {
    fun addBankAccount(account: BankAccount) {
        val sql =
            "INSERT INTO bank_accounts (account_number, account_holder, bank_name, balance, pin) VALUES (?, ?, ?, ?, ?)"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, account.getAccountNumber())
                stmt.setString(2, account.getAccountHolder())
                stmt.setString(3, account.getBankName())
                stmt.setDouble(4, account.getBalance())
                stmt.setString(5, account.getPin())
                stmt.executeUpdate()
            }
        }
    }

    fun updateAccountBalance(accountNumber: String, newBalance: Double): Boolean {
        val sql = "UPDATE bank_accounts SET balance = ? WHERE account_number = ?"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setDouble(1, newBalance)
                stmt.setString(2, accountNumber)
                val rowsUpdated = stmt.executeUpdate()
                return rowsUpdated > 0
            }
        }
    }

    fun updatePin(accountNumber: String, newPin: String): Boolean {
        val sql = "UPDATE bank_accounts SET pin = ? WHERE account_number = ?"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, newPin)
                stmt.setString(2, accountNumber)
                val rowsUpdated = stmt.executeUpdate()
                return rowsUpdated > 0
            }
        }
    }

    fun getBankAccount(accountNumber: String): BankAccount? {
        val sql = "SELECT * FROM bank_accounts WHERE account_number = ?"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, accountNumber)
                stmt.executeQuery().use { rs ->
                    return if (rs.next()) {
                        val bankAccount = BankAccount()
                        bankAccount.setAccountNumber(rs.getString("account_number"))
                        bankAccount.setAccountHolder(rs.getString("account_holder"))
                        bankAccount.setBankName(rs.getString("bank_name"))
                        bankAccount.setBalance(rs.getDouble("balance"))
                        bankAccount.setPin(rs.getString("pin"))
                        bankAccount
                    } else null
                }
            }
        }
    }

    fun getSecretKey(id: Int): String? {
        val sql = "SELECT * FROM secure_keys WHERE id = ?"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rs ->
                    return if (rs.next()) {
                        rs.getString("key")
                    } else null
                }
            }
        }
    }

    fun deleteAccount(accountNumber: String): Boolean {
        val sql = "DELETE FROM bank_accounts WHERE account_number = ?"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, accountNumber)
                val rowsDeleted = stmt.executeUpdate()
                return rowsDeleted > 0
            }
        }
    }

    fun loadAccounts(): List<BankAccount> {
        val accounts: ArrayList<BankAccount> = ArrayList()
        val sql = "SELECT * FROM bank_accounts"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        val bankAccount = BankAccount()
                        bankAccount.setAccountNumber(rs.getString("account_number"))
                        bankAccount.setAccountHolder(rs.getString("account_holder"))
                        bankAccount.setBankName(rs.getString("bank_name"))
                        bankAccount.setBalance(rs.getDouble("balance"))
                        bankAccount.setPin(rs.getString("pin"))
                        accounts.add(bankAccount)
                    }
                }
            }
        }
        return accounts
    }

    fun addTransaction(transaction: Transaction) {
        val sql = "INSERT INTO transactions (account, date, type, description, amount) VALUES (?, ?, ?, ?, ?)"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, transaction.account)
                stmt.setDate(2, java.sql.Date.valueOf(transaction.date))
                stmt.setString(3, transaction.type.name)
                stmt.setString(4, transaction.description)
                stmt.setDouble(5, transaction.amount)
                stmt.executeUpdate()
            }
        }
    }

    fun getTransactionsForAccount(accountNumber: String): List<Transaction> {
        val sql = "SELECT * FROM transactions WHERE account = ?"
        val transactions = mutableListOf<Transaction>()
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, accountNumber)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        transactions.add(
                            Transaction(
                                id = rs.getInt("id"),
                                account = rs.getString("account"),
                                date = rs.getDate("date").toLocalDate(),
                                type = TransactionType.valueOf(rs.getString("type")),
                                description = rs.getString("description"),
                                amount = rs.getDouble("amount")
                            )
                        )
                    }
                }
            }
        }
        return transactions
    }

    fun addSavingsGoal(accountNumber: String, goalName: String, targetAmount: Double): Boolean {
        val sql = "INSERT INTO savings_accounts (account_number, goal_name, target_amount, saved_amount) VALUES (?, ?, ?, 0.0)"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, accountNumber)
                stmt.setString(2, goalName)
                stmt.setDouble(3, targetAmount)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun getSavingsGoals(accountNumber: String): List<SavingsGoal> {
        val sql = "SELECT * FROM savings_accounts WHERE account_number = ?"
        val savingsGoals = mutableListOf<SavingsGoal>()
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, accountNumber)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        savingsGoals.add(
                            SavingsGoal(
                                id = rs.getInt("id"),
                                accountNumber = rs.getString("account_number"),
                                goalName = rs.getString("goal_name"),
                                targetAmount = rs.getDouble("target_amount"),
                                savedAmount = rs.getDouble("saved_amount")
                            )
                        )
                    }
                }
            }
        }
        return savingsGoals
    }

    fun getSavingsGoal(goalId: Int): SavingsGoal? {
        val sql = "SELECT * FROM savings_accounts WHERE id = ?"
//        val savingsGoals = mutableListOf<SavingsGoal>()
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, goalId)
                stmt.executeQuery().use { rs ->
                    return if (rs.next()) {
                        SavingsGoal(
                            id = rs.getInt("id"),
                            accountNumber = rs.getString("account_number"),
                            goalName = rs.getString("goal_name"),
                            targetAmount = rs.getDouble("target_amount"),
                            savedAmount = rs.getDouble("saved_amount")
                        )
                    } else null
                }
            }
        }
//        return savingsGoals
    }

    fun depositToSavings(accountNumber: String, goalId: Int, amount: Double): Boolean {
        val sql = "UPDATE savings_accounts SET saved_amount = saved_amount + ? WHERE id = ? AND account_number = ?"
        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setDouble(1, amount)
                stmt.setInt(2, goalId)
                stmt.setString(3, accountNumber)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun withdrawFromSavings(accountNumber: String, goalId: Int, amount: Double): Boolean {
        val sqlCheck = "SELECT saved_amount FROM savings_accounts WHERE id = ? AND account_number = ?"
        val sqlUpdate = "UPDATE savings_accounts SET saved_amount = saved_amount - ? WHERE id = ? AND account_number = ?"

        DatabaseHelper.getConnection().use { connection ->
            connection.prepareStatement(sqlCheck).use { stmt ->
                stmt.setInt(1, goalId)
                stmt.setString(2, accountNumber)
                val rs = stmt.executeQuery()
                if (rs.next() && rs.getDouble("saved_amount") >= amount) {
                    connection.prepareStatement(sqlUpdate).use { updateStmt ->
                        updateStmt.setDouble(1, amount)
                        updateStmt.setInt(2, goalId)
                        updateStmt.setString(3, accountNumber)
                        return updateStmt.executeUpdate() > 0
                    }
                }
            }
        }
        return false
    }

}

