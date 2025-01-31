package org.example

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.Stage

class AlertPopupController {
    @FXML
    lateinit var okButton: Button

    @FXML
    lateinit var messageLabel: Label

    @FXML
    fun initialize(message: String, popupStage: Stage) {
        messageLabel.text = message
        okButton.setOnAction {
            popupStage.close()
        }
    }
}