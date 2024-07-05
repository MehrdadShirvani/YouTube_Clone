package Client;

import Shared.Models.Account;
import javafx.event.ActionEvent;

import java.io.IOException;

public class RetryPageController {
    public void retryAction(ActionEvent actionEvent) {
        try {
            Client newClient = new Client();
            Account account = YouTube.client.getAccount();

            newClient.sendLoginRequest(account.getUsername() , account.getPassword());
            YouTube.client = newClient;

        } catch (IOException ignored) {
            //TODO Ehsan: make pop up error to show that still there is no connection
        }
    }
}
