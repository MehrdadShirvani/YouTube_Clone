package Client;

import Shared.Models.Account;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class SignUpController {
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
    TextField emailTextField;
    @FXML
    PasswordField passwordField;
    @FXML
    VBox signupVbox;
    @FXML
    GridPane designGridBox;
    @FXML
    VBox designVBox;
    Label firstNameLabel;
    Label usernameLabel;
    Label passwordLabel;
    Label emailLabel;

    public void initialize() {
        backgroundBorderPane.prefWidthProperty().bind(backgroundGridPane.widthProperty().multiply((double) 892 / 1007));
        backgroundBorderPane.prefHeightProperty().bind(backgroundGridPane.heightProperty().multiply((double) 455 / 641));
        designVBox.setOpacity(0);
        signupVbox.setOpacity(0);
        Platform.runLater(this::fadeIn);

    }

    private void fadeIn(List<Node> nodes) {
        for (Node node : nodes) {
            TranslateTransition translate = new TranslateTransition(Duration.seconds(0.8), node);
            node.setTranslateX(100);
            translate.setByX(-100);  // move left by 100 units
            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), node);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);  // fade in
            translate.play();
            fade.play();
        }
    }

    private void fadeIn() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(designVBox);
        nodes.add(signupVbox);
        for (Node node : nodes) {
            TranslateTransition translate = new TranslateTransition(Duration.seconds(0.8), node);
            node.setTranslateX(100);
            translate.setByX(-100);  // move left by 100 units
            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), node);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);  // fade in
            translate.play();
            fade.play();
        }
    }

    private void fadeOut(List<Node> nodes) {
        TranslateTransition lastTranslate = null;
        for (Node node : nodes) {
            TranslateTransition translate = new TranslateTransition(Duration.seconds(0.8), node);
            translate.setByX(-100);  // move left by 100 units
            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), node);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);  // fade out
            translate.play();
            fade.play();
            lastTranslate = translate;
//            translate.setOnFinished(event ->YouTube.changeScene("login-view.fxml"));
//            node.managedProperty().bind(node.visibleProperty());
        }

        lastTranslate.setOnFinished(event -> YouTube.changeScene("login-view.fxml"));
    }

    public void signup(ActionEvent event) {
        if (firstNameTextField.getText().isBlank() || usernameTextField.getText().isBlank()) {
            firstNameChanged(null);
            usernameChanged(null);
            return;
        }

        if (!Shared.Utils.TextValidator.validateEmail(emailTextField.getText())) {
            emailChanged(null);
            return;
        }

        String passwordMessage = Shared.Utils.TextValidator.validatePassword(passwordField.getText());
        if (!passwordMessage.isBlank()) {
            passwordChanged(null);
            return;
        }

        //Username Uniqueness
        if (!YouTube.client.sendCheckEmailUnique(usernameTextField.getText()))
        {
            //TODO Ehsan -> Style the Message Box
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username is not unique");
            alert.showAndWait();
            return;
        }

        //email Uniqueness
        if (!YouTube.client.sendCheckEmailUnique(emailTextField.getText()))
        {
            //TODO Ehsan -> Style the Message Box
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Email is not unique");
            alert.showAndWait();
            return;
        }

        Account account = new Account(firstNameTextField.getText(), lastNameTextField.getText(), usernameTextField.getText(), emailTextField.getText(), passwordField.getText(), null);
        if(!YouTube.client.sendSignupRequest(account))
        {
            //TODO Stylize
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Sign Up Failed. Please contact our support team");
            alert.showAndWait();
            return;
        }

        YouTube.changeScene("home-view.fxml");

    }

    public void login(ActionEvent event) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(designVBox);
        nodes.add(signupVbox);
        fadeOut(nodes);
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

    public void emailChanged(KeyEvent keyEvent) {
        if (!Shared.Utils.TextValidator.validateEmail(emailTextField.getText())) {
            if (signupVbox.getChildren().contains(emailLabel)) {
                return;
            }

            emailLabel = new Label();
            emailLabel.setText("Should be valid");
            emailLabel.setTextFill(Paint.valueOf("#ffffff"));
            signupVbox.getChildren().add(signupVbox.getChildren().indexOf(emailTextField) + 1, emailLabel);
        } else {
            signupVbox.getChildren().remove(emailLabel);
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
        if (!error.isBlank()) {
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
}