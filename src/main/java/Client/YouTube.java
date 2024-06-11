package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class YouTube extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(YouTube.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1007, 641);
        stage.setMinWidth(1007);
        stage.setMinHeight(641);
        stage.setTitle("YouTube");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}