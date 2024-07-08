package Client;

import Shared.Models.Channel;
import Shared.Models.Playlist;
import Shared.Models.Video;
import Shared.Utils.DateFormats;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ChannelViewController {
    public Label authorLabel;
    public Label subsLabel;
    public VBox otherNoContent;
    public VBox myNoContent2;
    public VBox otherNoContent2;
    public VBox myNoContent3;
    public VBox otherNoContent3;
    public VBox myNoContent;
    public Label descLabel;
    public Label subsLabel2;
    public Label NumberOfVideosLabel;
    public Label NumberOfViewsLabel;
    public Label joinLabel;
    public Label locationLabel;
    public WebView headerPictureWebView;
    @FXML
    ToggleButton subsButton;
    @FXML
    SVGPath subsIcon;
    @FXML
    WebView profileWebView;
    @FXML
    WebView myNoContentWebView;
    @FXML
    WebView myNoContentWebView2;
    @FXML
    WebView myNoContentWebView3;
    @FXML
    WebView otherNoContentWebView;
    @FXML
    WebView otherNoContentWebView2;
    @FXML
    WebView otherNoContentWebView3;
    @FXML
    VBox homeVBox;
    @FXML
    VBox contentVBox;
    @FXML
    HBox recentHBox;
    @FXML
    HBox popularHBox;
    @FXML
    FlowPane videosFlowPane;
    @FXML
    VBox playListVBox;
    @FXML
    FlowPane playListFlowPane;
    private Rectangle maskcommentProfileRec;
    private boolean isChannelSubscribed;
    private Channel channel;
    private HomeController homeController;

    public void initialize() throws IOException {
        //Mask
        maskcommentProfileRec = new Rectangle(80, 80);
        maskcommentProfileRec.setArcWidth(80);
        maskcommentProfileRec.setArcHeight(80);
        profileWebView.setClip(maskcommentProfileRec);

        //Icon
        subsButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                subsIcon.setContent("M10 20H14C14 21.1 13.1 22 12 22C10.9 22 10 21.1 10 20ZM20 17.35V19H4V17.35L6 15.47V10.32C6 7.40001 7.56 5.10001 10 4.34001V3.96001C10 2.54001 11.49 1.46001 12.99 2.20001C13.64 2.52001 14 3.23001 14 3.96001V4.35001C16.44 5.10001 18 7.41001 18 10.33V15.48L20 17.35ZM19 17.77L17 15.89V10.42C17 7.95001 15.81 6.06001 13.87 5.32001C12.61 4.79001 11.23 4.82001 10.03 5.35001C8.15 6.11001 7 7.99001 7 10.42V15.89L5 17.77V18H19V17.77Z");
            } else {
                subsIcon.setContent("");
            }
        });
        String myURL = HomeController.class.getResource("my-no-content.html").toExternalForm();
        String otherURL = HomeController.class.getResource("other-no-content.html").toExternalForm();
        System.out.println(myURL);

        myNoContentWebView.getEngine().load(myURL);
        otherNoContentWebView.getEngine().load(otherURL);
        myNoContentWebView.setPageFill(Color.TRANSPARENT);
        otherNoContentWebView.setPageFill(Color.TRANSPARENT);

        myNoContentWebView2.getEngine().load(myURL);
        otherNoContentWebView2.getEngine().load(otherURL);
        myNoContentWebView2.setPageFill(Color.TRANSPARENT);
        otherNoContentWebView2.setPageFill(Color.TRANSPARENT);

        myNoContentWebView3.getEngine().load(myURL);
        otherNoContentWebView3.getEngine().load(otherURL);
        myNoContentWebView3.setPageFill(Color.TRANSPARENT);
        otherNoContentWebView3.setPageFill(Color.TRANSPARENT);

        for (int i = 0; i < 10; ++i) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            recentHBox.getChildren().add(fxmlLoader.load());
            ((SmallVideoView) fxmlLoader.getController()).setPref(0.8, false, false);
        }
        for (int i = 0; i < 10; ++i) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            popularHBox.getChildren().add(fxmlLoader.load());
            ((SmallVideoView) fxmlLoader.getController()).setPref(0.8, false, false);
        }
        for (int i = 0; i < 10; ++i) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            videosFlowPane.getChildren().add(fxmlLoader.load());
            ((SmallVideoView) fxmlLoader.getController()).setPref(1, false, false);
        }

        //Delete the vboxs if the content is found(my/other)
        //If no content is found then delete the contentVBox
        //If content is found:
//        homeVBox.setAlignment(Pos.CENTER_LEFT);

    }


    public void setChannel(Channel channel, HomeController homeController) throws IOException {
        this.channel = channel;
        this.homeController = homeController;

        if (Objects.equals(channel.getChannelId(), YouTube.client.getAccount().getChannelId())) {
            ((VBox) otherNoContent.getParent()).getChildren().remove(otherNoContent);
            ((VBox) otherNoContent2.getParent()).getChildren().remove(otherNoContent2);
            ((VBox) otherNoContent3.getParent()).getChildren().remove(otherNoContent3);
        } else {
            ((VBox) myNoContent.getParent()).getChildren().remove(myNoContent);
            ((VBox) myNoContent2.getParent()).getChildren().remove(myNoContent2);
            ((VBox) myNoContent3.getParent()).getChildren().remove(myNoContent3);
        }

        authorLabel.setText(channel.getName());
        descLabel.setText(channel.getDescription());
        String location = channel.getLocation();
        locationLabel.setText(location.isBlank() ? "Iran" : location);
        joinLabel.setText(DateFormats.formatTimestamp(channel.getCreatedDateTime()));

        DecimalFormat formatter = new DecimalFormat("#,###");
        Long numberOfViews = YouTube.client.getAllViewsOfChannel(channel.getChannelId());
        NumberOfViewsLabel.setText(formatter.format(numberOfViews) + " views ");
        Long numberOfVideos = YouTube.client.getCountOfVideosOfChannel(channel.getChannelId());
        NumberOfVideosLabel.setText(formatter.format(numberOfVideos) + " videos ");
        try {
            //TODO the big channel image
            Path path = new File("src/main/resources/Client/image-view.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            headerPictureWebView.getEngine().loadContent(htmlContent.replace("@url", "http://localhost:2131/image/H_" + channel.getChannelId()));

            path = new File("src/main/resources/Client/profile.html").toPath();
            htmlContent = new String(Files.readAllBytes(path));
            profileWebView.getEngine().loadContent(htmlContent.replace("@id", channel.getChannelId() + ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Channel> subscribers = YouTube.client.getChannelSubscribers(channel.getChannelId());
        if (subscribers != null) {
            subsLabel.setText(subscribers.size() + " subscribers");
            subsLabel2.setText(subscribers.size() + " subscribers ");
        } else {
            subsLabel.setText("No subscribers");
            subsLabel2.setText("No subscribers");
        }
        boolean isSelf = Objects.equals(channel.getChannelId(), YouTube.client.getAccount().getChannelId());
        Task<Void> loaderView = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    List<Video> popularVideos = YouTube.client.getMostPopularVideosOfChannel(channel.getChannelId(), 10, 1);
                    List<Video> recentVideos = YouTube.client.getRecentVideosOfChannel(channel.getChannelId(), 10, 1);
                    if (recentVideos.isEmpty() && popularVideos.isEmpty()) {
//                        homeVBox.setAlignment(Pos.CENTER);
                        ((VBox) contentVBox.getParent()).getChildren().remove(contentVBox);
                    } else {
                        if (isSelf)
                            ((VBox) myNoContent.getParent()).getChildren().remove(myNoContent);
                        else
                            ((VBox) otherNoContent.getParent()).getChildren().remove(otherNoContent);

                    }
                    popularHBox.getChildren().clear();
                    for (Video recVideo : popularVideos) {
                        if (!isSelf && recVideo.getPrivate()) {
                            continue;
                        }

                        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
                        Parent smallVideo = null;
                        try {
                            smallVideo = fxmlLoader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        SmallVideoView smallVideoController = fxmlLoader.getController();
                        smallVideoController.setVideo(recVideo, homeController);
                        popularHBox.getChildren().add(smallVideo);
                    }

                    recentHBox.getChildren().clear();
                    for (Video recVideo : recentVideos) {
                        if (!isSelf && recVideo.getPrivate()) {
                            continue;
                        }

                        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
                        Parent smallVideo = null;
                        try {
                            smallVideo = fxmlLoader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        SmallVideoView smallVideoController = fxmlLoader.getController();
                        smallVideoController.setVideo(recVideo, homeController);
                        recentHBox.getChildren().add(smallVideo);
                    }
                    List<Playlist> playlists = YouTube.client.getPlaylistsOfChannel(channel.getChannelId(), isSelf);
                    playlists.add(new Playlist());
                    playlists.add(new Playlist());
                    playlists.add(new Playlist());
                    playlists.add(new Playlist());
                    if (!playlists.isEmpty())
                        if (isSelf)
                            ((VBox) myNoContent3.getParent()).getChildren().remove(myNoContent3);
                        else
                            ((VBox) otherNoContent3.getParent()).getChildren().remove(otherNoContent3);

                    if (playlists != null) {
                        for (Playlist playlist : playlists) {
                            FXMLLoader fxmlLoader = new FXMLLoader(ChannelViewController.class.getResource("small-playlist-view.fxml"));
                            Parent smallPlayList = null;
                            try {
                                smallPlayList = fxmlLoader.load();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            SmallPlayListController smallVideoController = fxmlLoader.getController();
                            smallVideoController.setPlayList(playlist);
                            playListFlowPane.getChildren().add(smallPlayList);
                        }
                    }

                    videosFlowPane.getChildren().clear();
                    List<Video> allVideos = YouTube.client.getVideosOfChannel(channel.getChannelId(), 20, 1);
                    if (!allVideos.isEmpty())
                        if (isSelf)
                            ((VBox) myNoContent2.getParent()).getChildren().remove(myNoContent2);
                        else
                            ((VBox) otherNoContent2.getParent()).getChildren().remove(otherNoContent2);
                    for (Video video : allVideos) {
                        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
                        try {
                            videosFlowPane.getChildren().add(fxmlLoader.load());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ((SmallVideoView) fxmlLoader.getController()).setPref(1, false, false);
                        ((SmallVideoView) fxmlLoader.getController()).setVideo(video, homeController);
                    }


                    if (Objects.equals(channel.getChannelId(), YouTube.client.getAccount().getChannelId())) {
                        subsButton.setVisible(false);
                    } else {
                        isChannelSubscribed = YouTube.client.isSubscribedToChannel(channel.getChannelId());
                        if (isChannelSubscribed) {
                            subsIcon.setContent("M10 20H14C14 21.1 13.1 22 12 22C10.9 22 10 21.1 10 20ZM20 17.35V19H4V17.35L6 15.47V10.32C6 7.40001 7.56 5.10001 10 4.34001V3.96001C10 2.54001 11.49 1.46001 12.99 2.20001C13.64 2.52001 14 3.23001 14 3.96001V4.35001C16.44 5.10001 18 7.41001 18 10.33V15.48L20 17.35ZM19 17.77L17 15.89V10.42C17 7.95001 15.81 6.06001 13.87 5.32001C12.61 4.79001 11.23 4.82001 10.03 5.35001C8.15 6.11001 7 7.99001 7 10.42V15.89L5 17.77V18H19V17.77Z");
                            subsButton.setText("Unsubscribe");
                        } else {
                            subsIcon.setContent("");
                            subsButton.setText("Subscribe");
                        }
                    }
                });
                return null;
            }
        };

        Thread thread = new Thread(loaderView);
        thread.setDaemon(true);
        thread.start();
    }

    public void subscribeToggleAction(ActionEvent actionEvent) {
        CompletableFuture.runAsync(() -> {
            try {
                if (isChannelSubscribed) {
                    YouTube.client.sendUnsubscribeRequest(YouTube.client.getAccount().getChannelId(), channel.getChannelId());
                    Platform.runLater(() -> {
                        subsIcon.setContent("");
                        subsButton.setText("Subscribe");
                    });
                } else {
                    YouTube.client.sendSubscribeRequest(YouTube.client.getAccount().getChannelId(), channel.getChannelId());
                    Platform.runLater(() -> {
                        subsIcon.setContent("M10 20H14C14 21.1 13.1 22 12 22C10.9 22 10 21.1 10 20ZM20 17.35V19H4V17.35L6 15.47V10.32C6 7.40001 7.56 5.10001 10 4.34001V3.96001C10 2.54001 11.49 1.46001 12.99 2.20001C13.64 2.52001 14 3.23001 14 3.96001V4.35001C16.44 5.10001 18 7.41001 18 10.33V15.48L20 17.35ZM19 17.77L17 15.89V10.42C17 7.95001 15.81 6.06001 13.87 5.32001C12.61 4.79001 11.23 4.82001 10.03 5.35001C8.15 6.11001 7 7.99001 7 10.42V15.89L5 17.77V18H19V17.77Z");
                        subsButton.setText("Unsubscribe");
                    });
                }

                isChannelSubscribed = !isChannelSubscribed;
                int subscribersCount = YouTube.client.getChannelSubscribers(channel.getChannelId()).size();
                Platform.runLater(() -> subsLabel.setText(subscribersCount + " subscribers "));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void createVideoAction(ActionEvent actionEvent) {
        homeController.setVideoEditingPage(null);
    }

    public void playlistCreateAction(ActionEvent actionEvent) {
        homeController.setPlaylistPage(null);
    }
}