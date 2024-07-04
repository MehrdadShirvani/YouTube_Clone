package Client;

import Shared.Models.Comment;
import Shared.Models.Reaction;
import Shared.Models.Video;
import Shared.Utils.DateFormats;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class VideoViewController {
    public Label titleLabel;
    public Label viewsLabel;
    public ToggleButton likeButton;
    public ToggleButton dislikeButton;
    public Button shareButton;
    public Label authorLabel;
    public Label subsLabel;
    public Button subsButton;
    public Label desLabel;
    public FlowPane sideBarFlow;
    public Label commentsLabel;
    @FXML
    BorderPane mainBorderPane;
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
    private Rectangle maskVideoRec;
    private Rectangle maskProfileRec;
    private Rectangle maskcommentProfileRec;
    private
    HashMap<Boolean,Short> isVideoLiked;
    private boolean isChannelSubscribed = false;
    private List<Comment> commentList;
    private List<Video> recommendedVideos;

    public void initialize() {
        //Pref of main border pain: 1084 * 664
        //bindings
//        rightVBox.prefWidthProperty().bind(mainBorderPane.widthProperty().divide(4));
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
    }
    public void setVideo(Video video, HomeController homeController)
    {
        titleLabel.setText(video.getName());
        authorLabel.setText(video.getChannel().getName());
        DecimalFormat formatter = new DecimalFormat("#,###");
        Long numberOfViews = YouTube.client.getViewsOfVideo(video.getVideoId());
        Long likeCount = YouTube.client.getLikesOfVideo(video.getVideoId());
        likeButton.setText(likeCount + "");
        viewsLabel.setText(formatter.format(numberOfViews) + " views . " + DateFormats.formatTimestamp(video.getCreatedDateTime()));
        desLabel.setText(video.getDescription());



        try {
            Path path = new File("src/main/resources/Client/profile.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            authorProfile.getEngine().loadContent(htmlContent.replace("@id", video.getChannelId()+""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        subsLabel.setText(YouTube.client.getChannelSubscribers(video.getChannelId()).size() + " subscribers ");


        try {
            Path path = new File("src/main/resources/Client/profile.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            commentProfile.getEngine().loadContent(htmlContent.replace("@id", YouTube.client.getAccount().getChannelId() + ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Path path = new File("src/main/resources/Client/video-player.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            videoWebView.getEngine().loadContent(htmlContent.replace("@id", video.getVideoId()+""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //addVideoView
        Task<Void> loaderView = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

//                isVideoLiked = YouTube.client.isVideoLiked(video.getVideoId());
                try {
//                    isVideoLiked.get(false);
                }
                catch (Exception ex)
                {
                    int a = 1;
                }
                commentList = YouTube.client.getCommentsOfVideo(video.getVideoId());
                //TODO -> get VideoCategories
                recommendedVideos = YouTube.client.searchVideo(null, "", 10,1);
                Platform.runLater(()->{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    for(Video recVideo : recommendedVideos)
                    {
                        if(!Objects.equals(recVideo.getVideoId(), video.getVideoId()))
                        {
                            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
                            Parent smallVideo = null;
                            try {
                                smallVideo = fxmlLoader.load();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            SmallVideoView controller = fxmlLoader.getController();
                            controller.setVideo(recVideo, homeController);
                            homeVideosFlowPane.getChildren().add(smallVideo);
                        }
                    }
                });
//                isChannelSubscribed = YouTube.client.isSubscribedToChannel(video.getChannelId());
                return null;
            }
        };

        Thread thread = new Thread(loaderView);
        thread.setDaemon(true);
        thread.start();
    }
    public void hi(ActionEvent event) {
        System.out.println(videoWebView.getWidth());
    }

    public void commentChanged(KeyEvent keyEvent) {
        commentButton.setDisable(commentTextField.getText().isBlank());
    }
}
