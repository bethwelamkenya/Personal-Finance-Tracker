package org.example

import javafx.animation.ScaleTransition
import javafx.animation.TranslateTransition
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration

class LoginController {
    @FXML
    lateinit var dontHaveAccount: Text

    @FXML
    lateinit var loginButton: Button

    @FXML
    private lateinit var accountNumberField: TextField

    @FXML
    private lateinit var pinField: PasswordField

    // Inject AppDependencies
    lateinit var dependencies: AppDependencies

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies
        // Access dependencies via the AppDependencies instance
        val tracker = this.dependencies.tracker
        val bankingTracker = this.dependencies.bankingTracker
        val dbConnector = this.dependencies.dbConnector
        val key = this.dependencies.key

        // Check if properties are initialized
//        if (!tracker.isInitialized || !::bankingTracker.isInitialized || !::dbConnector.isInitialized) {
//            throw IllegalStateException("Dependencies are not initialized!")
//        }

        val scaleTransition = ScaleTransition(Duration.millis(300.0), loginButton)
        // Set up the hover effect
        loginButton.setOnMouseEntered {
            scaleTransition.toX = 1.05
            scaleTransition.toY = 1.05
            scaleTransition.play()
        }

        // Reset the scale when the mouse exits
        loginButton.setOnMouseExited {
            scaleTransition.toX = 1.0
            scaleTransition.toY = 1.0
            scaleTransition.play()
        }
    }

    @FXML
    fun handleLogin() {

        animateClick(loginButton)

        val accountNumber = accountNumberField.text
        val pin = pinField.text

        if (dependencies.bankingTracker.logIn(
                dependencies.encryptionHelper.encryptText(accountNumber, dependencies.key),
                dependencies.encryptionHelper.encryptText(pin, dependencies.key),
                dependencies.dbConnector
            )
        ) {
            val stage = (accountNumberField.scene.window as Stage)
            val loader = FXMLLoader(javaClass.getResource("/home.fxml"))

            val root: Parent = loader.load()
            val scene = Scene(root)

            val controller: HomeController = loader.getController()
            controller.initialize(dependencies)

            stage.title = "Personal Finance Tracker - Home"
            stage.scene = scene
            stage.isResizable = true  // Allow resizing for the main window
        } else {
            println("Please enter both account number and PIN.")
        }
    }

    private fun animateClick(node: Node) {
        val translateTransition = TranslateTransition(Duration.millis(100.0), node)
        translateTransition.byY = 5.0
        translateTransition.cycleCount = 2
        translateTransition.isAutoReverse = true
        translateTransition.play()
    }

    @FXML
    fun createAccount(mouseEvent: MouseEvent) {
        val stage = (accountNumberField.scene.window as Stage)
        val loader = FXMLLoader(javaClass.getResource("/create_account.fxml"))

        val root: Parent = loader.load()
        val scene = Scene(root)

        val controller: CreateAccountController = loader.getController()
        controller.initialize(dependencies)

        stage.title = "Personal Finance Tracker - Create Account"
        stage.scene = scene
        stage.isResizable = false  // Allow resizing for the main window
    }
}