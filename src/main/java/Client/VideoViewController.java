package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;

public class VideoViewController {
    @FXML
    BorderPane mainBorderPane;
    @FXML
    VBox rightVBox;
    @FXML
    WebView videoWebView;
    private Rectangle maskVideoRec;

    public void initialize() {
        //bindings
        rightVBox.prefWidthProperty().bind(mainBorderPane.widthProperty().divide(4));
        videoWebView.prefHeightProperty().bind(videoWebView.widthProperty().multiply(0.562));
        //mask video with rec
        Platform.runLater(() -> {
            maskVideoRec = new Rectangle(videoWebView.getWidth(), videoWebView.getWidth() * 0.562);
            maskVideoRec.setArcWidth(25);
            maskVideoRec.setArcHeight(25);
            videoWebView.setClip(maskVideoRec);
            maskVideoRec.widthProperty().bind(videoWebView.widthProperty());
            maskVideoRec.heightProperty().bind(videoWebView.heightProperty());
        });
        videoWebView.getEngine().load("http://example.com");
    }

    public void hi(ActionEvent event) {
        System.out.println(videoWebView.getWidth());
    }
}
