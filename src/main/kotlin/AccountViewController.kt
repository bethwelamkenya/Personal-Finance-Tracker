package org.example

import javafx.fxml.FXML
import javafx.scene.control.Label
import java.security.Key

class AccountViewController {
    @FXML private lateinit var accountNumberLabel: Label
    @FXML private lateinit var balanceLabel: Label

    lateinit var tracker: FinanceTracker
    lateinit var bankingTracker: BankingTracker
    lateinit var dbConnector: DBConnector
    lateinit var savingsTracker: SavingsTracker
    lateinit var encryptionHelper: EncryptionHelper
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

        val account = bankingTracker.getActiveAccount()!!
        accountNumberLabel.text = encryptionHelper.decryptText(account.getAccountNumber(), key)
        balanceLabel.text = account.getBalance().toString()
    }
}