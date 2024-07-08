package Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.awt.event.ActionEvent;

public class TwoFactorAuthenticatorController {
    @FXML
    private TextArea codeField;

    public void useEmailButton(ActionEvent actionEvent) {
        YouTube.changeScene("two-factor-email-view.fxml");
    }


    public void loginButton(ActionEvent actionEvent) {
        String codeString = codeField.getText();
        int code = Integer.getInteger(codeString);

        if (YouTube.client.authenticatorVerify(code)) {
            YouTube.changeScene("home-view.fxml");

        } else {
            //TODO : pop up error
        }
    }
}
