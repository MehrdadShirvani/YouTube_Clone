package Client;

import Shared.Utils.CacheUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.IOException;


public class TwoFactorAuthenticatorController {
    @FXML
    private TextArea codeField;

    public void useEmailButton(ActionEvent actionEvent) {
        YouTube.changeScene("two-factor-email-view.fxml");
    }


    public void loginButton(ActionEvent actionEvent) {
        String codeString = codeField.getText();
        int code = Integer.parseInt(codeString);

        if (YouTube.client.authenticatorVerify(code)) {
            try {
                CacheUtil.cacheAccount(YouTube.client.getAccount());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            YouTube.changeScene("home-view.fxml");

        } else {
            //TODO : pop up error
        }
    }
}
