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

}

