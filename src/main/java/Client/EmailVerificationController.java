package Client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
}
