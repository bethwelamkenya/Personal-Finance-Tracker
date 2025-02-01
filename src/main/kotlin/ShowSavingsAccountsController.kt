package org.example

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.text.Text
import javafx.stage.Stage
import java.time.LocalDate

class ShowSavingsAccountsController {
    @FXML
    lateinit var idColumn: TableColumn<SavingsGoal, Int>

    @FXML
    lateinit var amountColumn: TableColumn<SavingsGoal, Double>

    @FXML
    lateinit var goalColumn: TableColumn<SavingsGoal, Double>

    @FXML
    lateinit var targetColumn: TableColumn<SavingsGoal, String>

    @FXML
    lateinit var closeButton: Button

    @FXML
    lateinit var savingsGoalsTitle: Text

    @FXML
    lateinit var savingsTable: TableView<SavingsGoal>

    @FXML
    fun initialize(dependencies: AppDependencies, popupStage: Stage) {
        val account = dependencies.bankingTracker.getActiveAccount()!!
        val savingsGoals = dependencies.dbConnector.getSavingsGoals(account.getAccountNumber())
        savingsGoalsTitle.text = "Savings for ${
            dependencies.encryptionHelper.decryptText(
                account.getAccountNumber(),
                dependencies.key
            )
        }"
        // Decrypt account numbers before adding to the table
        val decryptedSavingsGoals = savingsGoals.map { saving ->
            saving.copy(
                accountNumber = dependencies.encryptionHelper.decryptText(saving.accountNumber, dependencies.key)
            )
        }

//        savingsTable.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN

        // Set the data to the table
        savingsTable.items = FXCollections.observableArrayList(decryptedSavingsGoals)

        closeButton.setOnAction {
            popupStage.close()
        }
    }
}