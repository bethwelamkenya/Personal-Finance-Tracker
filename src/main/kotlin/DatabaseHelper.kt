package org.example

import java.sql.Connection
import java.sql.DriverManager

object DatabaseHelper {
    private const val DB_URL = "jdbc:mysql://localhost:3306/finance_tracker" // Replace `finance_tracker` with your database name
    private const val DB_USER = "root" // Replace with your MySQL username
    private const val DB_PASSWORD = "9852" // Replace with your MySQL password
    fun getConnection(): Connection {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD).apply {
//            createTables()
        }
    }

//    private fun createTables() {
//        use { connection ->
//            connection.createStatement().use { stmt ->
//                stmt.executeUpdate(
//                    """
//                    CREATE TABLE IF NOT EXISTS bank_accounts (
//                        account_number VARCHAR(255) PRIMARY KEY,
//                        account_holder VARCHAR(255) NOT NULL,
//                        bank_name VARCHAR(255) NOT NULL,
//                        balance DOUBLE NOT NULL,
//                        pin VARCHAR(255) NOT NULL
//                    );
//
//                    CREATE TABLE IF NOT EXISTS transactions (
//                        id INT PRIMARY KEY AUTO_INCREMENT,
//                        account VARCHAR(255) NOT NULL,
//                        date DATE NOT NULL,
//                        type VARCHAR(255) NOT NULL,
//                        description TEXT,
//                        amount DOUBLE NOT NULL,
//                        FOREIGN KEY (account) REFERENCES bank_accounts (account_number)
//                    );
//                    """.trimIndent()
//                )
//            }
//        }
//    }
}
