package org.example

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import java.security.Key

class LoginController {
    @FXML
    lateinit var loginButton: Button
    @FXML
    private lateinit var accountNumberField: TextField
    @FXML
    private lateinit var pinField: PasswordField

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
    }

    @FXML
    fun handleLogin() {
        val accountNumber = accountNumberField.text
        val pin = pinField.text

        if (bankingTracker.logIn(
                encryptionHelper.encryptText(accountNumber, key),
                encryptionHelper.encryptText(pin, key),
                dbConnector
            )
        ) {
            // Load the account view
            val loader = FXMLLoader(javaClass.getResource("/account_view.fxml"))
            val root: Parent = loader.load()
            val controller = loader.getController<AccountViewController>()
            controller.initialize(tracker, bankingTracker, dbConnector, savingsTracker, encryptionHelper, key)

            val scene = accountNumberField.scene
            scene.root = root
        } else {
            println("Please enter both account number and PIN.")
        }
    }
}