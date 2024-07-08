package Client;

import Shared.Utils.QrCodeGenerator;
import com.google.zxing.WriterException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.event.ActionEvent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
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

        Image qrCodeImage = new Image(new File(QrCodeGenerator.FILE_PATH).toURI().toString());

        QrCodeImageView.setImage(qrCodeImage);
    }


    public void setUpButton(ActionEvent actionEvent) {
        String authCode = authCodeField.getText();

        int code = Integer.parseInt(authCode);

        Boolean isVerified = YouTube.client.authenticatorVerify(code);

        if (isVerified) {
            try {
                Path path = Paths.get(QrCodeGenerator.FILE_PATH);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            YouTube.changeScene("home-view.fxml");

        } else {
            //TODO Ehsan : make a pop up error

        }
    }
}
