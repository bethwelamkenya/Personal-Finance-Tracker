package org.example

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.Stage

class MainApp : Application() {

    override fun start(primaryStage: Stage) {

        val tracker = FinanceTracker()
        val bankingTracker = BankingTracker()
        val dbConnector = DBConnector()
        val savingsTracker = SavingsTracker()
        val encryptionHelper = EncryptionHelper()

        // 1. Generate a new AES key (you can store it securely in your app)
        val key = encryptionHelper.getKeyFromString(dbConnector.getSecretKey(1)!!)

        // Load the FXML file
        val loader = FXMLLoader(javaClass.getResource("/login.fxml"))
        val root: Parent = loader.load()

        val controller = loader.getController<LoginController>()
        controller.initialize(tracker, bankingTracker, dbConnector, savingsTracker, encryptionHelper, key)

        // Set up the scene
        val scene = Scene(root, 300.0, 200.0)
        primaryStage.title = "Personal Finance Tracker - Login"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}