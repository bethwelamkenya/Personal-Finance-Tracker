package org.example

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.stage.Stage

class CreateGoalController {
    @FXML
    lateinit var nameBox: HBox

    @FXML
    lateinit var nameLabel: Label

    @FXML
    lateinit var nameField: TextField

    @FXML
    lateinit var targetBox: HBox

    @FXML
    lateinit var targetLabel: Label

    @FXML
    lateinit var targetField: TextField

    @FXML
    lateinit var createButton: Button

    @FXML
    fun initialize(onCreate: (name: String, target: Double) -> Unit) {
//        nameLabel.prefWidthProperty().bind(nameBox.widthProperty().multiply(0.4)) // 40%
//        nameField.prefWidthProperty().bind(nameBox.widthProperty().multiply(0.6)) // 60%
//
//        targetLabel.prefWidthProperty().bind(targetBox.widthProperty().multiply(0.4)) // 40%
//        targetField.prefWidthProperty().bind(targetBox.widthProperty().multiply(0.6)) // 60%

        val textFormatter = TextFormatter<String> { change ->
            if (change.controlNewText.matches(Regex("\\d*\\.?\\d*"))) change else null
        }

        targetField.textFormatter = textFormatter

        createButton.setOnAction {
            if (nameField.text != null && targetField.text != null) {
                onCreate(nameField.text, targetField.text.toDouble())
            } else {
                println("Invalid input. Please try again.")
            }
        }
    }
}