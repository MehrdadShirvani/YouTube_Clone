package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


public class TwoFactorEmailController {
    @FXML
    private TextArea codeField;
    @FXML
    private Label emailLabel;
    private int codeDigit;


    public void initialize() {
        emailLabel.setText(YouTube.client.getAccount().getEmail());
        this.codeDigit = YouTube.client.twoFactorEmailSend();

    }
    public void useAuthenticatorButton(ActionEvent actionEvent) {
        YouTube.changeScene("two-factor-authenticator-view.fxml");
    }


    public void loginButton(ActionEvent actionEvent) {
        String codeString = codeField.getText();
        int code = Integer.parseInt(codeString);

        if (code == codeDigit) {
            YouTube.changeScene("home-view.fxml");

        } else {
            //TODO : pop up error
        }
    }


    public void resendEmail(ActionEvent actionEvent) {
        this.codeDigit = YouTube.client.twoFactorEmailSend();
    }
}
