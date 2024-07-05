package Client;

import Shared.Models.Account;
import Shared.Utils.CacheUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

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

    public static void main(String[] args) throws IOException {
        client = new Client();

        try {
            if (CacheUtil.isCacheAvailable() & CacheUtil.isCacheUnchanged()) {
                System.out.println("Login with cached account !");
                Account account = CacheUtil.readAccountFromCache();
                boolean isLoggedIn = client.sendLoginRequest(account.getEmail() , account.getPassword());

                if (isLoggedIn) {
                    viewName = "home-view.fxml";
                } else {
                    System.out.println("Failed to Login using cached account !");
                    System.out.println("Login Normally !");
                    viewName = "login-view.fxml";
                }
            } else {
                viewName = "login-view.fxml";
                System.out.println("Login Normally !");

            }
        } catch (IOException e) {
            System.out.println("An error occurred");
            throw new RuntimeException(e);
        }

        launch();
    }
}