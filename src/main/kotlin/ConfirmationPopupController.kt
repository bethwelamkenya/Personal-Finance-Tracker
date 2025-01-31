package org.example

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.Stage

class ConfirmationPopupController {
    @FXML
    lateinit var noButton: Button

    @FXML
    lateinit var yesButton: Button

    @FXML
    lateinit var messageLabel: Label

    @FXML
    fun initialize(message: String, popupStage: Stage, onConfirm: () -> Unit) {
        messageLabel.text = message
        yesButton.setOnAction {
            onConfirm()
            popupStage.close()
        }
        noButton.setOnAction {
            popupStage.close()
        }
    }
}
