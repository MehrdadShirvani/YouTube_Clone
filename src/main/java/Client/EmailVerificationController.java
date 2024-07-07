package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.awt.event.ActionEvent;

public class EmailVerificationController {
    @FXML
    private Label emailSentLabel;

    @FXML
    private TextField tokenField;

    public void initialize() {
        String email = YouTube.client.getAccount().getEmail();
        String emailSentText = "We've sent an token to " + email;

        emailSentLabel.setText(emailSentText);
    }

    public void verifyKey(ActionEvent actionEvent) {
        String inputToken = tokenField.getText();
        String originalToken = YouTube.client.verifyEmail();

        if (inputToken == null) {
            //TODO Ehsan : make a pop up error that input token can't be null
            return;
        }

        if (inputToken.equals(originalToken)) {
            //TODO : change to next scene
        } else {
            //TODO Ehsan : pop up error that it's not equals
        }
    }
}
