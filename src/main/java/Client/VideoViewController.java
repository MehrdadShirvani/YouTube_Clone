package Client;

import Shared.Models.Video;
import Shared.Utils.DateFormats;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Date;

public class VideoViewController {
    public Label titleLabel;
    public Label viewsLabel;
    public ToggleButton likeButton;
    public ToggleButton dislikeButton;
    public Button shareButton;
    public Label authorLabel;
    public Label subsLabel;
    public Button subsButton;
    @FXML
    BorderPane mainBorderPane;
    @FXML
    VBox rightVBox;
    @FXML
    WebView videoWebView;
    @FXML
    WebView authorProfile;
    @FXML
    WebView commentProfile;
    @FXML
    TextField commentTextField;
    @FXML
    Button commentButton;
    @FXML
    ScrollPane leftScrollPane;
    private Rectangle maskVideoRec;
    private Rectangle maskProfileRec;
    private Rectangle maskcommentProfileRec;
    private WebEngine engine;

    public void initialize() {
        //Pref of main border pane: 1084 * 664
        //bindings
        rightVBox.prefWidthProperty().bind(mainBorderPane.widthProperty().divide(4));
        videoWebView.prefHeightProperty().bind(videoWebView.widthProperty().multiply(0.562));
        //mask video with rec
        Platform.runLater(() -> {
            maskVideoRec = new Rectangle(videoWebView.getWidth(), videoWebView.getWidth() * 0.562);
            maskVideoRec.setArcWidth(25);
            maskVideoRec.setArcHeight(25);
            maskVideoRec.setFill(Paint.valueOf("#303030"));
            videoWebView.setClip(maskVideoRec);
            maskVideoRec.widthProperty().bind(videoWebView.widthProperty());
            maskVideoRec.heightProperty().bind(videoWebView.heightProperty());
        });
        videoWebView.getEngine().load("http://example.com");
        //Mask author profile
        maskProfileRec = new Rectangle(48, 48);
        maskProfileRec.setArcWidth(48);
        maskProfileRec.setArcHeight(48);
        authorProfile.setClip(maskProfileRec);
        //Mask comment profile
        maskcommentProfileRec = new Rectangle(48, 48);
        maskcommentProfileRec.setArcWidth(48);
        maskcommentProfileRec.setArcHeight(48);
        commentProfile.setClip(maskcommentProfileRec);
        //Arrow keys assign
        leftScrollPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP|| event.getCode() == KeyCode.LEFT||  event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.SPACE) {
                if( event.getCode() == KeyCode.LEFT)
                    engine.executeScript("player.forward(-10);");
                if( event.getCode() == KeyCode.RIGHT)
                    engine.executeScript("player.forward(10);");
                if( event.getCode() == KeyCode.UP)
                    engine.executeScript("player.increaseVolume(0.1);");
                if( event.getCode() == KeyCode.DOWN)
                    engine.executeScript("player.decreaseVolume(-0.1);");
                if( event.getCode() == KeyCode.SPACE)
                    engine.executeScript("player.togglePlay();");
                event.consume(); // Prevent default
            }
        });
    }
    public void setVideo(Video video)
    {
        titleLabel.setText(video.getName());
        authorLabel.setText(video.getChannel().getName());
        DecimalFormat formatter = new DecimalFormat("#,###");
        Long numberOfViews = YouTube.client.getViewsOfVideo(video.getVideoId());
        Long likeCount = YouTube.client.getLikesOfVideo(video.getVideoId());
        likeButton.setText(likeCount + "");
        viewsLabel.setText(formatter.format(numberOfViews) + " views . " + DateFormats.formatTimestamp(video.getCreatedDateTime()));

        String urlPhoto = HomeController.class.getResource("profile.html").toExternalForm();
        try {
            Path path = new File("src/main/resources/Client/profile.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            authorProfile.getEngine().loadContent(htmlContent.replace("@id", video.getChannelId()+""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        subsLabel.setText(YouTube.client.getChannelSubscribers(video.getChannelId()) + "");

//        try {
//            Path path = new File("src/main/resources/Client/profile.html").toPath();
//            String htmlContent = new String(Files.readAllBytes(path));
//            commentProfile.getEngine().loadContent(htmlContent.replace("@id", YouTube.client.getAccount().getChannelId() + ""));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        try {
            Path path = new File("src/main/resources/Client/video-player.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
//            videoWebView.getEngine().loadContent(htmlContent.replace("@id", video.getVideoId()+""));
            engine = videoWebView.getEngine();
            engine.loadContent(htmlContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void hi(ActionEvent event) {
        System.out.println(videoWebView.getWidth());
        engine.executeScript("player.play()");
    }

    public void commentChanged(KeyEvent keyEvent) {
        commentButton.setDisable(commentTextField.getText().isBlank());
    }
}
