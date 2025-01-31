package org.example

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.PasswordField
import javafx.stage.Stage

class ChangePinPopUpController {
    @FXML
    lateinit var oldPinField: PasswordField

    @FXML
    lateinit var checkPinButton: Button

    @FXML
    lateinit var checkPinBox: CheckBox

    @FXML
    lateinit var newPinField: PasswordField

    @FXML
    lateinit var changePinButton: Button

    private lateinit var dependencies: AppDependencies

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies
        newPinField.isDisable = true
        changePinButton.isDisable = true
    }

    fun isStrongPin(pin: String): Boolean {
        return pin.length >= 4 && pin.any { it.isDigit() }
    }

    fun checkPin(actionEvent: ActionEvent) {
        if (oldPinField.text.equals(
                dependencies.encryptionHelper.decryptText(
                    dependencies.bankingTracker.getActiveAccount()!!.getPin(), dependencies.key
                )
            )
        ) {
            checkPinBox.isSelected = true
            newPinField.isDisable = false
            changePinButton.isDisable = false
        } else {
            checkPinBox.isSelected = false
            newPinField.isDisable = true
            changePinButton.isDisable = true
        }
    }

    fun changePin(actionEvent: ActionEvent) {
        if (isStrongPin(newPinField.text)) {
            if (dependencies.dbConnector.updatePin(
                    dependencies.bankingTracker.getActiveAccount()!!.getAccountNumber(),
                    dependencies.encryptionHelper.encryptText(newPinField.text, dependencies.key)
                )
            ) {
                println("PIN updated successfully.")
                val stage = (changePinButton.scene.window as Stage)
                stage.close()
            } else {
                println("PIN update failed.")
            }
        } else {
            println("Invalid PIN. Must be at least 4 digits.")
        }
    }
}