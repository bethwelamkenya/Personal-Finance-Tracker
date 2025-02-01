package org.example

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage

class SavingsController {
    @FXML
    lateinit var withdraw: Button

    @FXML
    lateinit var deposit: Button

    @FXML
    lateinit var viewGoals: Button

    @FXML
    lateinit var createGoal: Button

    @FXML
    lateinit var savingsLabel: Label

    @FXML
    lateinit var backImage: ImageView

    @FXML
    lateinit var savingsBox: ChoiceBox<String>

    @FXML
    fun initialize(dependencies: AppDependencies) {
        var savingsGoals = dependencies.dbConnector.getSavingsGoals(
            dependencies.bankingTracker.getActiveAccount()!!.getAccountNumber()
        )
        val savingsGoalNames = ArrayList<String>()
        var selectedGoal = savingsGoals[0]
        for (i in savingsGoals) {
            savingsGoalNames.add(i.goalName)
        }

        savingsBox.items = FXCollections.observableArrayList(savingsGoalNames)
        savingsBox.value = savingsGoals[0].goalName
        savingsLabel.text = savingsBox.value

        savingsBox.setOnAction {
            val index = savingsGoalNames.indexOf(savingsBox.value)
            selectedGoal = savingsGoals[index]
            savingsLabel.text = savingsBox.value
        }

        backImage.setOnMouseClicked {
            val stage = (backImage.scene.window as Stage)
            val loader = FXMLLoader(javaClass.getResource("/home.fxml"))

            val root: Parent = loader.load()
            val scene = Scene(root)

            val controller: HomeController = loader.getController()
            controller.initialize(dependencies)

            stage.title = "Personal Finance Tracker - Home"
            stage.scene = scene
            stage.isResizable = true  // Allow resizing for the main window
        }

        withdraw.setOnAction {
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
                dependencies.savingsTracker.withdrawFromSavings(
                    dependencies.bankingTracker,
                    selectedGoal.id,
                    amount,
                    dependencies.tracker,
                    dependencies.dbConnector,
                    description
                )
                popupStage.close()
            }
            popupStage.showAndWait()
        }

        deposit.setOnAction {
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
                dependencies.savingsTracker.depositIntoSavings(
                    dependencies.bankingTracker,
                    selectedGoal.id,
                    amount,
                    dependencies.tracker,
                    dependencies.dbConnector,
                    description
                )
                popupStage.close()
            }
            popupStage.showAndWait()
        }

        createGoal.setOnAction {
            val loader = FXMLLoader(javaClass.getResource("/create_goal.fxml"))
            val root: Parent = loader.load()
            val controller: CreateGoalController = loader.getController()

            val popupStage = Stage().apply {
                initOwner(owner)
                initModality(Modality.APPLICATION_MODAL)
                title = "Create Goal"
                isResizable = false
                scene = Scene(root)

            }

            controller.initialize { name, target ->
                dependencies.savingsTracker.createSavingsGoal(
                    dependencies.bankingTracker,
                    name,
                    target,
                    dependencies.dbConnector
                )
                popupStage.close()

                savingsGoals = dependencies.dbConnector.getSavingsGoals(
                    dependencies.bankingTracker.getActiveAccount()!!.getAccountNumber()
                )
                savingsGoalNames.clear()
                for (i in savingsGoals) {
                    savingsGoalNames.add(i.goalName)
                }
                selectedGoal = savingsGoals[0]
                savingsBox.items = FXCollections.observableArrayList(savingsGoalNames)
                savingsBox.value = savingsGoals[0].goalName
                savingsLabel.text = savingsBox.value
            }
            popupStage.showAndWait()
        }

        viewGoals.setOnAction {
            val loader = FXMLLoader(javaClass.getResource("/show_savings_accounts.fxml"))
            val root: Parent = loader.load()
            val controller: ShowSavingsAccountsController = loader.getController()

            val popupStage = Stage().apply {
                initOwner(owner)
                initModality(Modality.APPLICATION_MODAL)
                title = "Savings"
                isResizable = true
                scene = Scene(root)
            }
            controller.initialize(dependencies, popupStage)
            popupStage.showAndWait()
        }
    }
}