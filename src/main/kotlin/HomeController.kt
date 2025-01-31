package org.example

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlin.system.exitProcess

class HomeController {
    @FXML
    lateinit var withdrawButton: Button

    @FXML
    lateinit var depositButton: Button

    @FXML
    lateinit var accountText: Text

    private lateinit var dependencies: AppDependencies

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies
        accountText.text = dependencies.encryptionHelper.decryptText(
            dependencies.bankingTracker.getActiveAccount()!!.getAccountNumber(), dependencies.key
        )
    }

    fun changePin(actionEvent: ActionEvent) {
        // Load the FXML file
        val loader = FXMLLoader(javaClass.getResource("/change_pin_popup.fxml"))
        val root: Parent = loader.load()

        // Get the controller and pass data
        val controller: ChangePinPopUpController = loader.getController()
        controller.initialize(dependencies)

        // Set up the popup stage
        val popupStage = Stage().apply {
            initOwner(owner)
            initModality(Modality.APPLICATION_MODAL) // Block interaction with parent window
            initStyle(StageStyle.UTILITY)
            isResizable = false
            this.title = title
            scene = Scene(root)
        }

        // Show the popup
        popupStage.showAndWait()
    }

    fun logOut(actionEvent: ActionEvent) {
        dependencies.bankingTracker.logOut()
        val stage = (accountText.scene.window as Stage)
        val loader = FXMLLoader(javaClass.getResource("/login.fxml"))

        val root: Parent = loader.load()
        val scene = Scene(root)

        val controller: LoginController = loader.getController()
        controller.initialize(dependencies)

        stage.title = "Personal Finance Tracker - LogIn"
        stage.scene = scene
        stage.isResizable = false  // Allow resizing for the main window
    }

    fun closeApp(actionEvent: ActionEvent) {
        exitProcess(0)
    }

    fun deleteAccount(actionEvent: ActionEvent) {
        val loader = FXMLLoader(javaClass.getResource("/confirmation_popup.fxml"))
        val root: Parent = loader.load()
        val controller: ConfirmationPopupController = loader.getController()

        val popupStage = Stage().apply {
            initOwner(owner)
            initModality(Modality.APPLICATION_MODAL)
            title = "Confirmation"
            isResizable = false
            scene = Scene(root)
        }

        controller.initialize("Are you sure you want to delete account?", popupStage) {
            dependencies.bankingTracker.deleteAccount(
                dependencies.tracker, dependencies.dbConnector
            )
            val stage = (accountText.scene.window as Stage)
            val logInLoader = FXMLLoader(javaClass.getResource("/login.fxml"))

            val logInRoot: Parent = logInLoader.load()
            val scene = Scene(logInRoot)

            val logInController: LoginController = logInLoader.getController()
            logInController.initialize(dependencies)

            stage.title = "Personal Finance Tracker - LogIn"
            stage.scene = scene
            stage.isResizable = false  // Allow resizing for the main window
        }
        popupStage.showAndWait()
    }

    fun depositMoney(actionEvent: ActionEvent) {
        val loader = FXMLLoader(javaClass.getResource("/deposit_withdraw.fxml"))
        val root: Parent = loader.load()
        val controller: DepositWithdrawController = loader.getController()

        val popupStage = Stage().apply {
            initOwner(owner)
            initModality(Modality.APPLICATION_MODAL)
            title = "Deposit Money"
            isResizable = false
            scene = Scene(root)
        }

        controller.initialize("Enter amount to deposit", "Deposit") { amount, description ->
            dependencies.bankingTracker.deposit(amount, description, dependencies.tracker, dependencies.dbConnector)
            popupStage.close()
        }
        popupStage.showAndWait()
    }

    fun withdrawMoney(actionEvent: ActionEvent) {
        val loader = FXMLLoader(javaClass.getResource("/deposit_withdraw.fxml"))
        val root: Parent = loader.load()
        val controller: DepositWithdrawController = loader.getController()

        val popupStage = Stage().apply {
            initOwner(owner)
            initModality(Modality.APPLICATION_MODAL)
            title = "Withdraw Money"
            isResizable = false
            scene = Scene(root)

        }

        controller.initialize("Enter amount to withdraw", "Withdraw") { amount, description ->
            dependencies.bankingTracker.withdraw(amount, description, dependencies.tracker, dependencies.dbConnector)
            popupStage.close()
        }
        popupStage.showAndWait()
    }
}