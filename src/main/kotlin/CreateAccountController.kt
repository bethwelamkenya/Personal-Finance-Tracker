package org.example

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
import java.security.Key

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
    lateinit var bankName: Label

    @FXML
    lateinit var bankBox: HBox

    @FXML
    lateinit var holderField: TextField

    @FXML
    lateinit var holderLabel: Label

    @FXML
    lateinit var holderBox: HBox

    @FXML
    lateinit var numberField: TextField

    @FXML
    lateinit var numberLabel: Label

    @FXML
    lateinit var numberBox: HBox

    @FXML
    lateinit var backImage: ImageView

    lateinit var dependencies: AppDependencies

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies
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

    fun createAccount(actionEvent: ActionEvent) {
        val accountNumber = numberField.text
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