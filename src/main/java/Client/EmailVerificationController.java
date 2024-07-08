package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class EmailVerificationController {
    @FXML
    private Label emailSentLabel;

    @FXML
    private TextField tokenField;
    private String originalToken;

    public void initialize() {
        String email = YouTube.client.getAccount().getEmail();
        String emailSentText = "We've sent an token to " + email;

        emailSentLabel.setText(emailSentText);

        originalToken = YouTube.client.verifyEmail();
    }

    public void verifyKey(ActionEvent actionEvent) {
        String inputToken = tokenField.getText();

        if (inputToken == null) {
            //TODO Ehsan : make a pop up error that input token can't be null
            return;
        }

        if (inputToken.equals(originalToken)) {
            YouTube.changeScene("home-view.fxml");
        } else {
            //TODO Ehsan : pop up error that it's not equals
        }
    }
}
