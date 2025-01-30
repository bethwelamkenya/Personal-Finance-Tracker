package org.example

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class MainApp : Application() {

    override fun start(primaryStage: Stage) {
        // Initialize dependencies
//        val tracker = FinanceTracker()
//        val bankingTracker = BankingTracker()
//        val dbConnector = DBConnector()
//        val savingsTracker = SavingsTracker()
//        val encryptionHelper = EncryptionHelper()
//        val key = encryptionHelper.getKeyFromString(dbConnector.getSecretKey(1)!!)

        // Initialize dependencies
        val dependencies = AppDependencies().apply {
            key = encryptionHelper.getKeyFromString(dbConnector.getSecretKey(1)!!)
        }
        // Load the FXML file and inject dependencies into the controller
        val loader = FXMLLoader(javaClass.getResource("/login.fxml"))
        val root: Parent = loader.load()

        val loginController: LoginController = loader.getController()
        loginController.initialize(dependencies)
        // Set up the stage
        val loginStage = Stage().apply {
            title = "Personal Finance Tracker - Login"
            isResizable = false
            scene = Scene(root)
        }

        loginStage.show()
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}