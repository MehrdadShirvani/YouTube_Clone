package Client;

import Shared.Models.Account;
import javafx.event.ActionEvent;

import java.io.IOException;

public class RetryPageController {
    public void retryAction(ActionEvent actionEvent) {
        //TODO Ehsan: make a loading circle while clicking on retry button
        try {
            String viewName;
            Client newClient = new Client();

            if (YouTube.client != null) {
                if (YouTube.client.getAccount() != null ) {
                    viewName = newClient.sendLoginRequest(YouTube.client.getAccount().getUsername() , YouTube.client.getAccount().getPassword()) ? "home-view.fxml" : "login-view.fxml";
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
            e.printStackTrace();
            //TODO Ehsan: make pop up error to show that still there is no connection
        }
    }
}
