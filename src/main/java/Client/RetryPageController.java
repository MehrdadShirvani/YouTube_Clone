package Client;

import Shared.Models.Account;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;

public class RetryPageController {
    @FXML
    WebView webView;
    @FXML
    Button retryButton;
    private PauseTransition loadingButtonTransition;

    public void initialize() {
        String myURL = HomeController.class.getResource("lost.html").toExternalForm();
        webView.setPageFill(Color.TRANSPARENT);
        webView.getEngine().load(myURL);
        //Button cool-down
        loadingButtonTransition = new PauseTransition(Duration.seconds(2));
        loadingButtonTransition.setOnFinished(event -> retryButton.setDisable(false));
        //Webview rotation
        RotateTransition rotator = new RotateTransition(Duration.seconds(15), webView);
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(Animation.INDEFINITE);
        rotator.setByAngle(360);
        rotator.play();
    }

    public void retryAction(ActionEvent actionEvent) throws InterruptedException {
        //TODO Ehsan: make a loading circle while clicking on retry button
        retryButton.setDisable(true);
        loadingButtonTransition.play();
        try {
            String viewName;
            Client newClient = new Client();
            if (YouTube.client != null) {
                if (YouTube.client.getAccount() != null) {
                    viewName = newClient.sendLoginRequest(YouTube.client.getAccount().getUsername(), YouTube.client.getAccount().getPassword()) ? "home-view.fxml" : "login-view.fxml";
                    YouTube.client = newClient;

                } else {
                    YouTube.client = newClient;
                    viewName = YouTube.loginUsingCache();

                }

            } else {
                YouTube.client = newClient;
                viewName = YouTube.loginUsingCache();

            }

            YouTube.changeScene(viewName);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Still offline!");
        }
    }
}
