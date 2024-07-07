package Client;

import Shared.Utils.QrCodeGenerator;
import com.google.zxing.WriterException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;


public class AuthenticatorSetUpController{
    @FXML
    private ImageView QrCodeImageView;

    @FXML
    private TextField authCodeField;

    public void initialize() {
        HashMap<String , String> twoFactorData = YouTube.client.authenticatorAdd();
        String qrCodeData = twoFactorData.get(YouTube.client.getAccount().getEmail());

        try {
            QrCodeGenerator.generateQRCodeImage(qrCodeData);
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }

        Image qrCodeImage = new Image(QrCodeGenerator.FILE_PATH);

        QrCodeImageView.setImage(qrCodeImage);
    }


    public void setUpButton(ActionEvent actionEvent) {
        String authCode = authCodeField.getText();

        int code = Integer.getInteger(authCode);

        Boolean isVerified = YouTube.client.authenticatorVerify(code);

        if (isVerified) {
            //TODO : change the scene

        } else {
            //TODO Ehsan : make a pop up error

        }
    }
}
