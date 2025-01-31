package org.example

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.text.Text
import javafx.stage.Stage
import java.time.LocalDate

class ShowTransactionsController {
    @FXML
    lateinit var idColumn: TableColumn<Transaction, Int>

    @FXML
    lateinit var balanceLabel: Label

    @FXML
    lateinit var descriptionColumn: TableColumn<Transaction, String>

    @FXML
    lateinit var amountColumn: TableColumn<Transaction, Double>

    @FXML
    lateinit var dateColumn: TableColumn<Transaction, LocalDate>

    @FXML
    lateinit var typeColumn: TableColumn<Transaction, String>

    @FXML
    lateinit var accountColumn: TableColumn<Transaction, String>

    @FXML
    lateinit var closeButton: Button

    @FXML
    lateinit var transactionsTitle: Text

    @FXML
    lateinit var transactionTable: TableView<Transaction>

    @FXML
    fun initialize(dependencies: AppDependencies, popupStage: Stage) {
        val account = dependencies.bankingTracker.getActiveAccount()!!
        transactionsTitle.text = "Transactions for ${
            dependencies.encryptionHelper.decryptText(
                account.getAccountNumber(),
                dependencies.key
            )
        }"
        balanceLabel.text = account.getBalance().toString() + " " + account.getCurrency()
        val transactions = dependencies.dbConnector.getTransactionsForAccount(account.getAccountNumber())
        // Decrypt account numbers before adding to the table
        val decryptedTransactions = transactions.map { transaction ->
            transaction.copy(
                account = dependencies.encryptionHelper.decryptText(transaction.account, dependencies.key)
            )
        }

        transactionTable.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN

        // Set the data to the table
        transactionTable.items = FXCollections.observableArrayList(decryptedTransactions)

        closeButton.setOnAction {
            popupStage.close()
        }
    }
}