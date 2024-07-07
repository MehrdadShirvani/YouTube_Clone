package Client;

import Shared.Models.Account;
import Shared.Models.Video;
import Shared.Utils.CacheUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class YouTube extends Application {
    public static Stage primaryStage;
    public static Client client;
    private static String viewName;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(YouTube.class.getResource(viewName));
//        Scene scene = new Scene(fxmlLoader.load(), 1007, 641);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinWidth(1007);
        stage.setMinHeight(641);
        stage.setTitle("YouTube");
        stage.getIcons().add(new Image("file:youtubeicon.png"));
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("Current working directory: " + currentDirectory);
        stage.setScene(scene);
        stage.show();
    }

    public static void changeScene(String name) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(YouTube.class.getResource(name));
            Scene scene = new Scene(fxmlLoader.load());
            if (!name.equals("home-view.fxml"))
                primaryStage.setScene(scene);
            else
            {
                primaryStage.close();
                Stage stage = new Stage();
                primaryStage = stage;
                stage.setMinWidth(1280);
                stage.setMinHeight(720.0);
                stage.setTitle("YouTube");
                stage.getIcons().add(new Image("file:youtubeicon.png"));
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            client = new Client();

        } catch (Exception e) {
            viewName = "elements/retry-page.fxml";

            System.out.println("THERE IS NO CONNECTION !");
        }


        if (!Objects.equals(viewName, "elements/retry-page.fxml")) {
            try {
                viewName = loginUsingCache();
            } catch (Exception e) {
                System.out.println("An error occurred");
            }
        }

        launch();
    }


    public static String loginUsingCache() throws IOException {
        String viewAddress;

        if (CacheUtil.isCacheAvailable() & CacheUtil.isCacheUnchanged()) {
            System.out.println("Login with cached account !");
            Account account = CacheUtil.readAccountFromCache();
            boolean isLoggedIn = client.sendLoginRequest(account.getEmail() , account.getPassword());

            if (isLoggedIn) {
                viewAddress = "home-view.fxml";
            } else {
                System.out.println("Failed to Login using cached account !");
                System.out.println("Login Normally !");
                viewAddress = "login-view.fxml";
            }
        } else {
            viewAddress = "login-view.fxml";
            System.out.println("Login Normally !");
        }

        return viewAddress;
    }
}