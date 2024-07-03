package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class YouTube extends Application {
    public static Stage primaryStage;
    public static Client client;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(YouTube.class.getResource("home-view.fxml"));
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
        client = new Client();
        launch();
    }
}