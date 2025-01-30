package org.example

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage
import java.security.Key

class AccountViewController {
    @FXML
    private lateinit var accountNumberLabel: Label
    @FXML
    private lateinit var balanceLabel: Label

    lateinit var dependencies: AppDependencies

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies
        val account = dependencies.bankingTracker.getActiveAccount()!!
        accountNumberLabel.text =
            dependencies.encryptionHelper.decryptText(account.getAccountNumber(), dependencies.key)
        balanceLabel.text = account.getBalance().toString()
    }
}