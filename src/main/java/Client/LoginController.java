package Client;

import Shared.Utils.TextEncryptor;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class LoginController {
    @FXML
    GridPane designGridBox;
    @FXML
    VBox loginVbox;
    @FXML
    VBox designVBox;
    @FXML
    BorderPane backgroundBorderPane;
    @FXML
    GridPane backgroundGridPane;
    @FXML
    TextField emailTextField;
    @FXML
    PasswordField passwordField;

    public void initialize() {
        //Bindings
        backgroundBorderPane.prefWidthProperty().bind(backgroundGridPane.widthProperty().multiply((double) 892 / 1007));
        backgroundBorderPane.prefHeightProperty().bind(backgroundGridPane.heightProperty().multiply((double) 455 / 641));
        loginVbox.setOpacity(0);
        designVBox.setOpacity(0);
        Platform.runLater(() -> {
            fadeIn();
        });
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
        }
        lastTranslate.setOnFinished(event -> YouTube.changeScene("signup-view.fxml"));
    }

    private void fadeIn() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(designVBox);
        nodes.add(loginVbox);
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

    public void login(ActionEvent event) {
        if(!YouTube.client.sendLoginRequest(emailTextField.getText(), TextEncryptor.encrypt(passwordField.getText())))
        {
            //TODO Ehsan Stylize
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Login Failed");
            alert.showAndWait();
            return;
        }
        YouTube.changeScene("two-factor-authenticator-view.fxml");

    }

    public void signup(ActionEvent event) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(designVBox);
        nodes.add(loginVbox);
        fadeOut(nodes);
    }
}
