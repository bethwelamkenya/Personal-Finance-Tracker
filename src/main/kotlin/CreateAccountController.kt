package org.example

import javafx.animation.ScaleTransition
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.util.Duration

class CreateAccountController {

    @FXML
    lateinit var createButton: Button

    @FXML
    lateinit var pinField: PasswordField

    @FXML
    lateinit var pinLabel: Label

    @FXML
    lateinit var pinBox: HBox

    @FXML
    lateinit var bankField: TextField

    @FXML
    lateinit var bankLabel: Label

    @FXML
    lateinit var bankBox: HBox

    @FXML
    lateinit var holderField: TextField

    @FXML
    lateinit var holderLabel: Label

    @FXML
    lateinit var holderBox: HBox

    @FXML
    lateinit var accountField: TextField

    @FXML
    lateinit var accountLabel: Label

    @FXML
    lateinit var accountBox: HBox

    @FXML
    lateinit var backImage: ImageView

    private lateinit var dependencies: AppDependencies

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies

        accountLabel.prefWidthProperty().bind(accountBox.widthProperty().multiply(0.4)) // 40%
        accountField.prefWidthProperty().bind(accountBox.widthProperty().multiply(0.6)) // 60%

        pinLabel.prefWidthProperty().bind(pinBox.widthProperty().multiply(0.4)) // 40%
        pinField.prefWidthProperty().bind(pinBox.widthProperty().multiply(0.6)) // 60%

        holderLabel.prefWidthProperty().bind(holderBox.widthProperty().multiply(0.4)) // 40%
        holderField.prefWidthProperty().bind(holderBox.widthProperty().multiply(0.6)) // 60%

        bankLabel.prefWidthProperty().bind(bankBox.widthProperty().multiply(0.4)) // 40%
        bankField.prefWidthProperty().bind(bankBox.widthProperty().multiply(0.6)) // 60%

        val scaleTransition = ScaleTransition(Duration.millis(300.0), createButton)
        // Set up the hover effect
        createButton.setOnMouseEntered {
            scaleTransition.toX = 1.05
            scaleTransition.toY = 1.05
            scaleTransition.play()
        }

        // Reset the scale when the mouse exits
        createButton.setOnMouseExited {
            scaleTransition.toX = 1.0
            scaleTransition.toY = 1.0
            scaleTransition.play()
        }

        val scaleTransitionImage = ScaleTransition(Duration.millis(300.0), backImage)
        // Set up the hover effect
        backImage.setOnMouseEntered {
            scaleTransitionImage.toX = 1.05
            scaleTransitionImage.toY = 1.05
            scaleTransitionImage.play()
        }

        // Reset the scale when the mouse exits
        backImage.setOnMouseExited {
            scaleTransitionImage.toX = 1.0
            scaleTransitionImage.toY = 1.0
            scaleTransitionImage.play()
        }
    }

    fun back(mouseEvent: MouseEvent) {
        val stage = (backImage.scene.window as Stage)
        val loader = FXMLLoader(javaClass.getResource("/login.fxml"))

        val root: Parent = loader.load()
        val scene = Scene(root)

        val controller: LoginController = loader.getController()
        controller.initialize(dependencies)

        stage.title = "Personal Finance Tracker - LogIn"
        stage.scene = scene
        stage.isResizable = false  // Allow resizing for the main window
    }

    private fun isStrongPin(pin: String): Boolean {
        return pin.length >= 4 && pin.any { it.isDigit() }
    }

    fun createAccount(actionEvent: ActionEvent) {
        val accountNumber = accountField.text
        val accountHolder = holderField.text
        val bankName = bankField.text
        val pin = pinField.text
        if (accountHolder != null && bankName != null && pin != null) {
            if (isStrongPin(pin)) {
                dependencies.bankingTracker.createAccount(
                    dependencies.encryptionHelper.encryptText(accountNumber, dependencies.key),
                    accountHolder,
                    bankName,
                    dependencies.encryptionHelper.encryptText(pin, dependencies.key),
                    dependencies.dbConnector
                )

                val stage = (createButton.scene.window as Stage)
                val loader = FXMLLoader(javaClass.getResource("/home.fxml"))

                val root: Parent = loader.load()
                val scene = Scene(root)

                val controller: HomeController = loader.getController()
                controller.initialize(dependencies)

                stage.title = "Personal Finance Tracker - Home"
                stage.scene = scene
                stage.isResizable = true  // Allow resizing for the main window
            } else {
                println("Invalid PIN. Must be at least 4 digits.")
            }
        } else {
            println("Invalid input. Bank account not added.")
        }
    }
}