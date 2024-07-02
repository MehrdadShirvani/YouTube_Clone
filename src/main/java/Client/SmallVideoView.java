package Client;

import Shared.Models.Video;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

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
    HBox downHBox;
    @FXML
    WebView profileWebView;

    public SmallVideoView() {

    }

    public void setVideo(Video video) {
        //titleLabel.setText(video.getName());
        //authorLabel.setText(video.getChannel().getName());
        //DecimalFormat formatter = new DecimalFormat("#,###");
        //Long numberOfViews = YouTube.client.getViewsOfVideo(video.getVideoId());
        //viewsLabel.setText(formatter.format(numberOfViews));
        Task<Void> loaderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                Platform.runLater(() -> {

                    String urlThumbnail = HomeController.class.getResource("small-video-thumbnail.html").toExternalForm();

                    try {
                        Path path = new File("src/main/resources/Client/small-video-thumbnail.html").toPath();
                        String htmlContent = new String(Files.readAllBytes(path));
                        webView.getEngine().loadContent(htmlContent.replace("@id","37088"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String urlPhoto = HomeController.class.getResource("profile.html").toExternalForm();
                    try {
                        Path path = new File("src/main/resources/Client/profile.html").toPath();
                        String htmlContent = new String(Files.readAllBytes(path));
                        profileWebView.getEngine().loadContent(htmlContent.replace("@id", "37088"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                return  null;
            }
        };
        Thread loaderThread = new Thread(loaderTask);
        loaderThread.setDaemon(true); // Allow the thread to exit when the application exits
        loaderThread.start();

    }

    public void initialize() {
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

        // Thumb mask
        Rectangle thumbMaskRec = new Rectangle(248, 139.49);
        thumbMaskRec.setArcWidth(20);
        thumbMaskRec.setArcHeight(20);
        webView.setClip(thumbMaskRec);
        // Profile mask
        Rectangle profileMaskRec = new Rectangle(50,50);
        profileMaskRec.setArcHeight(50);
        profileMaskRec.setArcWidth(50);
        profileWebView.setClip(profileMaskRec);
        // load webview
        String url = HomeController.class.getResource("loading-small-video.html").toExternalForm();
        webView.getEngine().load(url);
        profileWebView.getEngine().load(url);
    }
}
