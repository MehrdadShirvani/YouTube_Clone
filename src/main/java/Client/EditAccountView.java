package Client;

import Shared.Models.Account;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditAccountView {

    public WebView profileWebView;
    public DatePicker DPBirthDate;
    @FXML
    BorderPane backgroundBorderPane;
    @FXML
    GridPane backgroundGridPane;
    @FXML
    Label designLabel;
    @FXML
    Button signupButton;
    @FXML
    TextField firstNameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    TextField usernameTextField;

    @FXML
    PasswordField passwordField;
    @FXML
    VBox signupVbox;
    @FXML
    GridPane designGridBox;
    @FXML
    VBox designVBox;
    @FXML
    HBox commentHBox;
    Label firstNameLabel;
    Label usernameLabel;
    Label passwordLabel;
    @FXML
    Label emailLabel;
    private Account currnetAccount;
    ExecutorService executorService = Executors.newCachedThreadPool();

    public void setAccount(Account account) throws IOException {
        this.currnetAccount = account;
        firstNameTextField.setText(account.getFirstName());
        lastNameTextField.setText(account.getLastName());
        usernameTextField.setText(account.getUsername());

        emailLabel.setText(account.getEmail());

        if(currnetAccount.getBirthDate() != null)
        {
            DPBirthDate.setValue(currnetAccount.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        // Profile mask
        Rectangle profileMaskRec = new Rectangle(50, 50);
        profileMaskRec.setArcHeight(50);
        profileMaskRec.setArcWidth(50);
        profileWebView.setClip(profileMaskRec);
        // load webview
        Path path = new File("src/main/resources/Client/image-view.html").toPath();
        String htmlContent = new String(Files.readAllBytes(path));
        profileWebView.getEngine().loadContent(htmlContent.replace("@url", "http://localhost:2131/image/P_" + account.getChannelId().toString()));

        //TODO  -> Mehrdad -> handle media server to handle lost video and pictures
    }
    public void firstNameChanged(KeyEvent keyEvent) {
        if (firstNameTextField.getText().isBlank()) {
            if (signupVbox.getChildren().contains(firstNameLabel)) {
                return;
            }

            firstNameLabel = new Label();
            firstNameLabel.setText("Shouldn't be empty");
            firstNameLabel.setTextFill(Paint.valueOf("#ffffff"));
            signupVbox.getChildren().add(signupVbox.getChildren().indexOf(firstNameTextField) + 1, firstNameLabel);
        } else {
            signupVbox.getChildren().remove(firstNameLabel);
        }
    }

    public void usernameChanged(KeyEvent keyEvent) {
        if (usernameTextField.getText().isBlank()) {
            if (signupVbox.getChildren().contains(usernameLabel)) {
                return;
            }

            usernameLabel = new Label();
            usernameLabel.setText("Shouldn't be empty");
            usernameLabel.setTextFill(Paint.valueOf("#ffffff"));
            signupVbox.getChildren().add(signupVbox.getChildren().indexOf(usernameTextField) + 1, usernameLabel);
        } else {
            signupVbox.getChildren().remove(usernameLabel);
        }
    }

    public void passwordChanged(KeyEvent keyEvent) {
        String error = Shared.Utils.TextValidator.validatePassword(passwordField.getText());
        if (!passwordField.getText().isBlank() || !error.isBlank()) {
            if (signupVbox.getChildren().contains(passwordLabel)) {
                return;
            }

            passwordLabel = new Label();
            passwordLabel.setText(error);//TODO Fix it to show all the text
            passwordLabel.setTextFill(Paint.valueOf("#ffffff"));
            signupVbox.getChildren().add(signupVbox.getChildren().indexOf(passwordField) + 1, passwordLabel);
        } else {
            signupVbox.getChildren().remove(passwordLabel);
        }
    }

    public void cancel(ActionEvent actionEvent) {
        YouTube.changeScene("home-view.fxml");
    }

    public void changeDetails(ActionEvent actionEvent) {
        if (firstNameTextField.getText().isBlank() || usernameTextField.getText().isBlank()) {
            firstNameChanged(null);
            usernameChanged(null);
            return;
        }


        String passwordMessage = Shared.Utils.TextValidator.validatePassword(passwordField.getText());
        if (!passwordField.getText().isBlank() && !passwordMessage.isBlank()) {
            passwordChanged(null);
            return;
        }

        //Username Uniqueness
        if (!Objects.equals(currnetAccount.getUsername(), usernameTextField.getText()) && !YouTube.client.sendCheckUsernameUnique(usernameTextField.getText()))
        {
            //TODO Ehsan -> Style the Message Box
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username is not unique");
            alert.showAndWait();
            return;
        }

        if(pictureFile != null)
        {
            uploadProfilePhoto(pictureFile, currnetAccount.getChannelId());
        }
        
        currnetAccount.setBirthDate(Date.from(DPBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        currnetAccount.setUsername(usernameTextField.getText());
        if(!passwordField.getText().isBlank())
        {
            //TODO encrypt
            currnetAccount.setPassword(passwordField.getText());
        }

        if(YouTube.client.sendAccountEditRequest(currnetAccount) == null)
        {
            //TODO Stylize
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Sign Up Failed. Please contact our support team");
            alert.showAndWait();
            return;
        }

        YouTube.changeScene("home-view.fxml");

    }

    private void uploadProfilePhoto(File pictureFile, Long channelId)
    {
            try {
                URL url = new URL("http://localhost:2131/upload");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "image/jpg|P_" + channelId);

                try (OutputStream outputStream = connection.getOutputStream();
                     FileInputStream fileInputStream = new FileInputStream(pictureFile)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();

                    int responseCode = connection.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                } finally {
                    connection.disconnect();
                }
            }catch (Exception ex)
            {

            }
    }

    File pictureFile;
    public void browsePicture(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Video File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);
        Stage dialogStage = new Stage();
        pictureFile = fileChooser.showOpenDialog(dialogStage);
        if(pictureFile != null)
        {
            Path path = new File("src/main/resources/Client/image-view.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            profileWebView.getEngine().loadContent(htmlContent.replace("@url","file:///" +  pictureFile.getAbsolutePath()));
        }
    }
}
