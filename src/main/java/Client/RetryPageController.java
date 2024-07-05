package Client;

import Shared.Models.Account;
import javafx.event.ActionEvent;

import java.io.IOException;

public class RetryPageController {
    public void retryAction(ActionEvent actionEvent) {
        //TODO Ehsan: make a loading circle while clicking on retry button
        try {
            boolean isLoggedIn;
            Client newClient = new Client();

            if (YouTube.client == null) {
                isLoggedIn = newClient.sendLoginRequest("" , "");

            } else {
                if (YouTube.client.getAccount() != null) {
                    isLoggedIn = newClient.sendLoginRequest(YouTube.client.getAccount().getUsername() , YouTube.client.getAccount().getPassword());

                } else {
                    isLoggedIn = newClient.sendLoginRequest("" , "");
                }
            }

            if (isLoggedIn) {
                YouTube.client = newClient;

                String homeViewName = "home-view.fxml";
                YouTube.changeScene(homeViewName);
            } else {
                String homeViewName = "login-view.fxml";
                YouTube.changeScene(homeViewName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //TODO Ehsan: make pop up error to show that still there is no connection
        }
    }
}
