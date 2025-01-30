// AppDependencies.kt
package org.example

import java.security.Key

class AppDependencies {
    val tracker = FinanceTracker()
    val bankingTracker = BankingTracker()
    val dbConnector = DBConnector()
    val savingsTracker = SavingsTracker()
    val encryptionHelper = EncryptionHelper()
    lateinit var key: Key // Initialize this in MainApp
}