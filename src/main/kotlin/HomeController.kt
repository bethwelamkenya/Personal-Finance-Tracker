package org.example

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.text.Text
import javafx.stage.Stage
import java.security.Key

class HomeController {
    @FXML
    lateinit var accountText: Text
    lateinit var dependencies: AppDependencies

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies
        accountText.text = dependencies.encryptionHelper.decryptText(
            dependencies.bankingTracker.getActiveAccount()!!.getAccountNumber(), dependencies.key
        )
    }
}