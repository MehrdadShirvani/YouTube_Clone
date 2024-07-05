package Client;

import Shared.Models.Account;
import javafx.event.ActionEvent;

import java.io.IOException;

public class RetryPageController {
    public void retryAction(ActionEvent actionEvent) {
        //TODO Ehsan: make a loading circle while clicking on retry button
        try {
            Client newClient = new Client();
            Account account = YouTube.client.getAccount();

            boolean isLoggedIn = newClient.sendLoginRequest(account.getUsername() , account.getPassword());

            if (isLoggedIn) {
                YouTube.client = newClient;

                String homeViewName = "home-view.fxml";
                YouTube.changeScene(homeViewName);
            } else {
                String homeViewName = "login-view.fxml";
                YouTube.changeScene(homeViewName);
            }

        } catch (IOException ignored) {
            //TODO Ehsan: make pop up error to show that still there is no connection
        }
    }
}
