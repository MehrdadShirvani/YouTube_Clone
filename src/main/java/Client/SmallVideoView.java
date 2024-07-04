package Client;

import Shared.Models.Video;
import Shared.Utils.DateFormats;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    Video video;
    HomeController homeController;
    private Timeline shimmerTimelineTitle;
    private Timeline shimmerTimelineViews;
    private Timeline shimmerTimelineAuthor;

    public SmallVideoView() {

    }

    public void setVideo(Video video, HomeController homeController) {
        this.video = video;
        this.homeController = homeController;
        titleLabel.setText(video.getName());
        authorLabel.setText(video.getChannel().getName());
        DecimalFormat formatter = new DecimalFormat("#,###");


        titleLabel.getStyleClass().clear();
        authorLabel.getStyleClass().clear();
        viewsLabel.getStyleClass().clear();
        titleLabel.getStyleClass().add("title");
        authorLabel.getStyleClass().add("caption");
        viewsLabel.getStyleClass().add("caption");
        shimmerTimelineTitle.stop();
        shimmerTimelineAuthor.stop();
        shimmerTimelineViews.stop();
        Task<Void> loaderTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                Platform.runLater(() -> {


                    try {
                        Path path = new File("src/main/resources/Client/small-video-thumbnail.html").toPath();
                        String htmlContent = new String(Files.readAllBytes(path));
                        webView.getEngine().loadContent(htmlContent.replace("@id", video.getVideoId() + ""));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Path path = new File("src/main/resources/Client/profile.html").toPath();
                        String htmlContent = new String(Files.readAllBytes(path));
                        profileWebView.getEngine().loadContent(htmlContent.replace("@id", video.getChannelId() + ""));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Long numberOfViews = YouTube.client.getViewsOfVideo(video.getVideoId());
                    Platform.runLater(() -> {
                        viewsLabel.setText(formatter.format(numberOfViews) + " views . " + DateFormats.toRelativeTime(video.getCreatedDateTime()));
                    });
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        Thread loaderThread = new Thread(loaderTask);
        loaderThread.setDaemon(true); // Allow the thread to exit when the application exits
        loaderThread.start();

    }

    public void initialize() {
        //Loading Animation
        shimmerTimelineTitle = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(titleLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(1), new KeyValue(titleLabel.opacityProperty(), 0))
        );
        shimmerTimelineTitle.setCycleCount(Timeline.INDEFINITE);
        shimmerTimelineTitle.setAutoReverse(true);
        shimmerTimelineTitle.play();

        shimmerTimelineViews = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(viewsLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(1), new KeyValue(viewsLabel.opacityProperty(), 0))
        );
        shimmerTimelineViews.setCycleCount(Timeline.INDEFINITE);
        shimmerTimelineViews.setAutoReverse(true);
        shimmerTimelineViews.play();
        shimmerTimelineAuthor = new Timeline(
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
        Rectangle profileMaskRec = new Rectangle(50, 50);
        profileMaskRec.setArcHeight(50);
        profileMaskRec.setArcWidth(50);
        profileWebView.setClip(profileMaskRec);
        // load webview
        String url = HomeController.class.getResource("loading-small-video.html").toExternalForm();
        webView.getEngine().load(url);
        profileWebView.getEngine().load(url);
    }

    public void clicked(MouseEvent mouseEvent) {
        homeController.setVideoPage(video);
    }
}
