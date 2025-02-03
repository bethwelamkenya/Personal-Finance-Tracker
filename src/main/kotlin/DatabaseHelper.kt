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

    private fun createTables(dbConnection: Connection) {
        dbConnection.use { connection ->
            connection.createStatement().use { stmt ->
                stmt.executeUpdate(
                    """
                    CREATE TABLE `bank_accounts` (
                      `account_number` varchar(255) NOT NULL,
                      `account_holder` varchar(255) NOT NULL,
                      `bank_name` varchar(255) NOT NULL,
                      `balance` double NOT NULL,
                      `pin` varchar(255) NOT NULL,
                      `currency` varchar(3) NOT NULL DEFAULT 'USD',
                      PRIMARY KEY (`account_number`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                    
                    CREATE TABLE `transactions` (
                      `id` int NOT NULL AUTO_INCREMENT,
                      `account` varchar(255) NOT NULL,
                      `date` date NOT NULL,
                      `type` varchar(255) NOT NULL,
                      `description` text,
                      `amount` double NOT NULL,
                      `currency` varchar(3) NOT NULL DEFAULT 'USD',
                      PRIMARY KEY (`id`),
                      KEY `transactions_ibfk_1` (`account`),
                      CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`account`) REFERENCES `bank_accounts` (`account_number`)
                    ) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                    
                    CREATE TABLE `savings_accounts` (
                      `id` int NOT NULL AUTO_INCREMENT,
                      `account_number` varchar(255) NOT NULL,
                      `goal_name` varchar(50) NOT NULL,
                      `target_amount` double NOT NULL,
                      `saved_amount` double NOT NULL DEFAULT '0',
                      PRIMARY KEY (`id`),
                      KEY `account_number` (`account_number`),
                      CONSTRAINT `savings_accounts_ibfk_1` FOREIGN KEY (`account_number`) REFERENCES `bank_accounts` (`account_number`)
                    ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                    
                    CREATE TABLE `secure_keys` (
                      `id` int NOT NULL AUTO_INCREMENT,
                      `key` varchar(45) DEFAULT NULL,
                      PRIMARY KEY (`id`)
                    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                    """.trimIndent()
                )
            }
        }
    }
}