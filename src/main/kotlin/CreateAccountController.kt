package org.example

import java.security.Key

class CreateAccountController {

    private lateinit var tracker: FinanceTracker
    private lateinit var bankingTracker: BankingTracker
    private lateinit var dbConnector: DBConnector
    private lateinit var savingsTracker: SavingsTracker
    private lateinit var encryptionHelper: EncryptionHelper
    lateinit var key: Key

    fun initialize(
        tracker: FinanceTracker,
        bankingTracker: BankingTracker,
        dbConnector: DBConnector,
        savingsTracker: SavingsTracker,
        encryptionHelper: EncryptionHelper,
        key: Key
    ) {
        this.tracker = tracker
        this.bankingTracker = bankingTracker
        this.dbConnector = dbConnector
        this.savingsTracker = savingsTracker
        this.encryptionHelper = encryptionHelper
        this.key = key
    }

}