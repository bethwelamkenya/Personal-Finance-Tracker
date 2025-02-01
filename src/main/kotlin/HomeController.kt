package org.example

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlin.system.exitProcess

class HomeController {
    @FXML
    lateinit var exportTransactionsButton: Button

    @FXML
    lateinit var viewTransactionsButton: Button

    @FXML
    lateinit var showBalance: CheckBox

    @FXML
    lateinit var amountText: Text

    @FXML
    lateinit var withdrawButton: Button

    @FXML
    lateinit var depositButton: Button

    @FXML
    lateinit var accountText: Label

    private lateinit var dependencies: AppDependencies
    private lateinit var account: BankAccount

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies
        account = dependencies.bankingTracker.getActiveAccount()!!
        accountText.text = dependencies.encryptionHelper.decryptText(account.getAccountNumber(), dependencies.key)
        amountText.text = "****"
        showBalance.isSelected = false

        showBalance.setOnAction {
            if (showBalance.isSelected) {
                amountText.text = account.getBalance().toString() + " " + account.getCurrency()
            } else {
                amountText.text = "****"
            }
        }

        exportTransactionsButton.setOnAction {
            val fileName = dependencies.tracker.exportTransactionsForAccount(
                account,
                dependencies.dbConnector,
                dependencies.encryptionHelper,
                dependencies.key
            )

            val loader = FXMLLoader(javaClass.getResource("/alert_popup.fxml"))
            val root: Parent = loader.load()
            val controller: AlertPopupController = loader.getController()

            val popupStage = Stage().apply {
                initOwner(owner)
                initModality(Modality.APPLICATION_MODAL)
                title = "Alert"
                isResizable = false
                scene = Scene(root)
            }
            controller.initialize("Transactions exported to $fileName", popupStage)
            popupStage.showAndWait()
        }

        viewTransactionsButton.setOnAction {
            val loader = FXMLLoader(javaClass.getResource("/show_transactions.fxml"))
            val root: Parent = loader.load()
            val controller: ShowTransactionsController = loader.getController()

            val popupStage = Stage().apply {
                initOwner(owner)
                initModality(Modality.APPLICATION_MODAL)
                title = "Transactions"
                isResizable = true
                scene = Scene(root)
            }
            controller.initialize(dependencies, popupStage)
            popupStage.showAndWait()
        }
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

    fun sendMoney(actionEvent: ActionEvent) {
        val loader = FXMLLoader(javaClass.getResource("/send_money.fxml"))
        val root: Parent = loader.load()
        val controller: SendMoneyController = loader.getController()

        val popupStage = Stage().apply {
            initOwner(owner)
            initModality(Modality.APPLICATION_MODAL)
            title = "Send Money"
            isResizable = false
            scene = Scene(root)
        }
        controller.initialize(dependencies, popupStage)
        popupStage.showAndWait()
    }

    fun savingsAccount(actionEvent: ActionEvent) {
        val stage = (accountText.scene.window as Stage)
        val loader = FXMLLoader(javaClass.getResource("/savings.fxml"))

        val root: Parent = loader.load()
        val scene = Scene(root)

        val controller: SavingsController = loader.getController()
        controller.initialize(dependencies)

        stage.title = "Personal Finance Tracker - Savings"
        stage.scene = scene
        stage.isResizable = false  // Allow resizing for the main window
    }
}