package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.awt.event.ActionEvent;

public class TwoFactorEmailController {
    @FXML
    private TextArea codeField;
    private int codeDigit;


    public void initialize() {
        this.codeDigit = YouTube.client.twoFactorEmailSend();

    }
    public void useAuthenticatorButton(ActionEvent actionEvent) {
        YouTube.changeScene("two-factor-authenticator-view.fxml");
    }


    public void loginButton(ActionEvent actionEvent) {
        String codeString = codeField.getText();
        int code = Integer.getInteger(codeString);

        if (code == codeDigit) {
            YouTube.changeScene("home-view.fxml");

        } else {
            //TODO : pop up error
        }
    }
}
