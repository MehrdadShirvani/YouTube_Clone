package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class EmailVerificationController {
    @FXML
    public Label emailSentLabel;
    @FXML
    public TextField tokenField;
    public Integer originalToken;
    @FXML
    public VBox designVBox;
    public BorderPane backgroundBorderPane;
    public GridPane backgroundGridPane;

    public void initialize() {
        String email = YouTube.client.getAccount().getEmail();
        String emailSentText = "We've sent an token to " + email;
        backgroundBorderPane.prefWidthProperty().bind(backgroundGridPane.widthProperty().multiply((double) 892 / 1007));
        backgroundBorderPane.prefHeightProperty().bind(backgroundGridPane.heightProperty().multiply((double) 455 / 641));
        emailSentLabel.setText(emailSentText);

        originalToken = YouTube.client.verifyEmail();
    }

    public void verifyKey(ActionEvent actionEvent) {
        String inputToken = tokenField.getText();
        Integer token = Integer.parseInt(inputToken);

        if (token.equals(originalToken)) {
            YouTube.changeScene("authenticator-add-view.fxml");
        } else {
            //TODO Ehsan : pop up error that it's not equals
        }
    }

    public void resendEmail(ActionEvent actionEvent) {
        originalToken = YouTube.client.verifyEmail();
    }
}
