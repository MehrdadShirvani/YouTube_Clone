package Client;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
        YouTube.changeScene("home-view.fxml");
    }

    public void login(ActionEvent event) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(designVBox);
        nodes.add(signupVbox);
        fadeOut(nodes);

    }
}