package org.example

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.stage.Stage

class SendMoneyController {

    @FXML
    lateinit var checkRecipient: CheckBox

    @FXML
    lateinit var checkButton: Button

    @FXML
    lateinit var recipientField: TextField

    @FXML
    lateinit var recipientLabel: Label

    @FXML
    lateinit var recipientBox: HBox

    @FXML
    lateinit var messageLabel: Label

    @FXML
    lateinit var amountBox: HBox

    @FXML
    lateinit var amountLabel: Label

    @FXML
    lateinit var amountField: TextField

    @FXML
    lateinit var descriptionBox: HBox

    @FXML
    lateinit var descriptionLabel: Label

    @FXML
    lateinit var descriptionField: TextField

    @FXML
    lateinit var sendButton: Button

    @FXML
    fun initialize(dependencies: AppDependencies, popupStage: Stage) {
        amountField.isDisable = true
        descriptionField.isDisable = true
        sendButton.isDisable = true
        var recipientAccount: BankAccount? = null

        val textFormatter = TextFormatter<String> { change ->
            if (change.controlNewText.matches(Regex("\\d*\\.?\\d*"))) change else null
        }

        amountField.textFormatter = textFormatter

        checkButton.setOnAction {
            recipientAccount = dependencies.dbConnector.getBankAccount(
                dependencies.encryptionHelper.encryptText(
                    recipientField.text!!,
                    dependencies.key
                )
            )
            if (recipientAccount != null) {
                amountField.isDisable = false
                checkRecipient.isSelected = true
                descriptionField.isDisable = false
                sendButton.isDisable = false
            } else {
                amountField.isDisable = true
                checkRecipient.isSelected = false
                descriptionField.isDisable = true
                sendButton.isDisable = true
                messageLabel.text = "Recipient not found."
            }
        }
        sendButton.setOnAction {
            if (recipientAccount != null && amountField.text.toDouble() >= 0 &&
                dependencies.bankingTracker.getActiveAccount()!!.getBalance() > amountField.text.toDouble()
            ) {
                dependencies.bankingTracker.transferFunds(
                    recipientAccount!!,
                    amountField.text.toDouble(),
                    dependencies.dbConnector,
                    dependencies.tracker,
                    descriptionField.text
                )
                popupStage.close()
            } else {
                messageLabel.text = "Invalid amount or insufficient balance."
            }
        }
    }
}