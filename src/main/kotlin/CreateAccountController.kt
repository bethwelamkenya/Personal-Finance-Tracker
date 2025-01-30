package org.example

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.security.Key

class CreateAccountController {

    lateinit var dependencies: AppDependencies

    @FXML
    fun initialize(dependencies: AppDependencies) {
        this.dependencies = dependencies
    }
}