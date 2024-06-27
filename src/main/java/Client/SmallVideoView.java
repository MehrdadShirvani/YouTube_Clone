package Client;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class SmallVideoView {
    @FXML
    WebView webView;
    @FXML
    Label titleLabel;
    @FXML
    Label viewsLabel;
    @FXML
    Label authorLabel;
    @FXML
    Circle profileCircle;
    public SmallVideoView()
    {

    }
    public void initialize(){
        //Loading Animation
        Timeline shimmerTimelineTitle = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(titleLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(1), new KeyValue(titleLabel.opacityProperty(), 0))
        );
        shimmerTimelineTitle.setCycleCount(Timeline.INDEFINITE);
        shimmerTimelineTitle.setAutoReverse(true);
        shimmerTimelineTitle.play();

        Timeline shimmerTimelineViews = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(viewsLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(1), new KeyValue(viewsLabel.opacityProperty(), 0))
        );
        shimmerTimelineViews.setCycleCount(Timeline.INDEFINITE);
        shimmerTimelineViews.setAutoReverse(true);
        shimmerTimelineViews.play();
        Timeline shimmerTimelineAuthor = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(authorLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(1), new KeyValue(authorLabel.opacityProperty(), 0))
        );
        shimmerTimelineAuthor.setCycleCount(Timeline.INDEFINITE);
        shimmerTimelineAuthor.setAutoReverse(true);
        shimmerTimelineAuthor.play();

//        Timeline shimmerTimeLineProfile = new Timeline(
//                new KeyFrame(Duration.ZERO, new KeyValue(profileCircle.fillProperty(), Color.valueOf("#212121"))),
//                new KeyFrame(Duration.seconds(1), new KeyValue(profileCircle.fillProperty(), Color.valueOf("#6C6C6C")))
//        );
//        shimmerTimeLineProfile.setCycleCount(Timeline.INDEFINITE);
//        shimmerTimeLineProfile.setAutoReverse(true);
//        shimmerTimeLineProfile.play();
        // Webview
        webView.setPageFill(Color.TRANSPARENT);
        String url = HomeController.class.getResource("loading-small-video.html").toExternalForm();
        webView.getEngine().load(url);
    }
}
