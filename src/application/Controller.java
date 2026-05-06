package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class Controller {

    @FXML
    private Label messageLabel;

    @FXML
    private void handleButtonClick() {
        messageLabel.setText("Button was clicked!");
    }

    @FXML
    private void handleClearClick() {
        messageLabel.setText("Welcome to JavaFX!");
    }
}