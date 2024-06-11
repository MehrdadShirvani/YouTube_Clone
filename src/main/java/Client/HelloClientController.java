package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloClientController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to Client JavaFX Application!");
    }
}