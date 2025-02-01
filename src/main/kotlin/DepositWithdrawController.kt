package org.example

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.stage.Stage

class DepositWithdrawController {
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
    lateinit var actionButton: Button

    fun initialize(message: String, button: String, onConfirm: (amount: Double, description: String) -> Unit) {
        messageLabel.text = message
        actionButton.text = button

//        amountLabel.prefWidthProperty().bind(amountBox.widthProperty().multiply(0.4)) // 40%
//        amountField.prefWidthProperty().bind(amountBox.widthProperty().multiply(0.6)) // 60%
//
//        descriptionLabel.prefWidthProperty().bind(descriptionBox.widthProperty().multiply(0.4)) // 40%
//        descriptionField.prefWidthProperty().bind(descriptionBox.widthProperty().multiply(0.6)) // 60%

        val textFormatter = TextFormatter<String> { change ->
            if (change.controlNewText.matches(Regex("\\d*\\.?\\d*"))) change else null
        }

        amountField.textFormatter = textFormatter

        actionButton.setOnAction {
            onConfirm(amountField.text.toDouble(), descriptionField.text)
        }
    }
}