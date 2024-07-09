package Client;

import Shared.Models.Account;
import Shared.Utils.TextEncryptor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditAccountView implements Initializable {

    public WebView profileWebView;
    public DatePicker DPBirthDate;
    public WebView channelWebView;
    public ToggleButton cbPremium;
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
    Label firstNameLabel;
    Label usernameLabel;
    Label passwordLabel;
    @FXML
    Label emailLabel;
    @FXML
    VBox backgroundGridPane;
    private Account currnetAccount;
    File pictureFile;
    File channelHeaderFile;

    ExecutorService executorService = Executors.newCachedThreadPool();
    public void setAccount(Account account) throws IOException {
        //Binding
        signupVbox.prefWidthProperty().bind(backgroundGridPane.widthProperty().multiply(0.75));
        signupVbox.prefHeightProperty().bind(backgroundGridPane.heightProperty().multiply(0.83));

        this.currnetAccount = account;
        firstNameTextField.setText(account.getFirstName());
        lastNameTextField.setText(account.getLastName());
        usernameTextField.setText(account.getUsername());

        emailLabel.setText("*Logged-in as: "+account.getEmail());

        cbPremium.setSelected(!(YouTube.client.getAccount().getPremiumExpirationDate() == null || YouTube.client.getAccount().getPremiumExpirationDate().before(new Date())));

        if(currnetAccount.getBirthDate() != null)
        {
            DPBirthDate.setValue(currnetAccount.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        // Profile mask
        Rectangle profileMaskRec = new Rectangle(50, 50);
        profileMaskRec.setArcHeight(50);
        profileMaskRec.setArcWidth(50);
        profileWebView.setClip(profileMaskRec);
        Path path = new File("src/main/resources/Client/image-view.html").toPath();
        String htmlContent = new String(Files.readAllBytes(path));
        profileWebView.getEngine().loadContent(htmlContent.replace("@url", "http://localhost:2131/image/P_" + account.getChannelId().toString()));
        Rectangle HeaderMaskRec = new Rectangle(50, 50);
        HeaderMaskRec.setArcHeight(50);
        HeaderMaskRec.setArcWidth(50);
        channelWebView.setClip(HeaderMaskRec);
        Path path2 = new File("src/main/resources/Client/image-view.html").toPath();
        String htmlContent2 = new String(Files.readAllBytes(path2));
        channelWebView.getEngine().loadContent(htmlContent2.replace("@url", "http://localhost:2131/image/H_" + account.getChannelId().toString()));
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
        if(passwordField.getText().isBlank())
        {
            error = "";
        }
        if (!error.isBlank()) {
            if (signupVbox.getChildren().contains(passwordLabel)) {
                passwordLabel.setText(error);
                Text text = new Text(passwordLabel.getText());
                text.setWrappingWidth(300);
                passwordLabel.setMinHeight(text.getLayoutBounds().getHeight());
                return;
            }

            passwordLabel = new Label();
            passwordLabel.setMaxWidth(300);
            passwordLabel.setWrapText(false);

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
            uploadProfilePhoto(pictureFile, currnetAccount.getChannelId(), false);
        }
        if(channelHeaderFile != null)
        {
            uploadProfilePhoto(channelHeaderFile, currnetAccount.getChannelId(), true);
        }
        if(DPBirthDate.getValue() != null)
        {
            currnetAccount.setBirthDate(Date.from(DPBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        else
        {
            currnetAccount.setBirthDate(null);
        }
        currnetAccount.setUsername(usernameTextField.getText());
        if(!passwordField.getText().isBlank())
        {
            currnetAccount.setPassword(TextEncryptor.encrypt(passwordField.getText()));
        }

        Date oneYearLater = Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        currnetAccount.setPremiumExpirationDate((cbPremium.isSelected())? new Timestamp(oneYearLater.getTime()):null);

        if(YouTube.client.sendAccountEditRequest(currnetAccount) == null)
        {
            //TODO Stylize
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Edit Failed. Please contact our support team");
            alert.showAndWait();
            return;
        }

        YouTube.changeScene("home-view.fxml");

    }

    private void uploadProfilePhoto(File pictureFile, Long channelId, boolean isHeader)
    {
            try {
                URL url = new URL("http://localhost:2131/upload");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "image/jpg|"+(isHeader?"H":"P")+"_" + channelId);

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

    public void browsePicture(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
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

    public void browseChannelHeader(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Header File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);
        Stage dialogStage = new Stage();
        channelHeaderFile = fileChooser.showOpenDialog(dialogStage);
        if(channelHeaderFile != null)
        {
            Path path = new File("src/main/resources/Client/image-view.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            channelWebView.getEngine().loadContent(htmlContent.replace("@url","file:///" +  channelHeaderFile.getAbsolutePath()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setAccount(YouTube.client.getAccount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
