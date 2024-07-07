package Client;

import Shared.Models.*;
import Shared.Utils.DateFormats;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoViewController {
    public Label titleLabel;
    public Label viewsLabel;
    public ToggleButton likeButton;
    public ToggleButton dislikeButton;
    public Button shareButton;
    public Label authorLabel;
    public Label subsLabel;
    public ToggleButton subsButton;
    public Label desLabel;
    public FlowPane sideBarFlow;
    public Label commentsLabel;
    public Button editVideoBtn;
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
    @FXML
    ScrollPane leftScrollPane;
    @FXML
    HBox commentBelowHBox;
    @FXML
    VBox leftVBox;
    @FXML
    HBox commentHBox;
    @FXML
    SVGPath likeIcon;
    @FXML
    SVGPath dislikeIcon;
    @FXML
    SVGPath subsIcon;
    private Rectangle maskVideoRec;
    private Rectangle maskProfileRec;
    private Rectangle maskcommentProfileRec;
    private WebEngine engine;
    private
    HashMap<Boolean, Short> isVideoLiked;
    private boolean isChannelSubscribed = false;
    private List<Comment> commentList;
    private List<Video> recommendedVideos;
    private Reaction currentReaction;
    private Long initialLikeCount = 0L;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void initialize() {
        //Pref of main border pane: 1084 * 664
        //bindings
//        rightVBox.prefWidthProperty().bind(mainBorderPane.widthProperty().divide(4));
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
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP || event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.SPACE) {
                if (event.getCode() == KeyCode.LEFT)
                    engine.executeScript("player.forward(-10);");
                if (event.getCode() == KeyCode.RIGHT)
                    engine.executeScript("player.forward(10);");
                if (event.getCode() == KeyCode.UP)
                    engine.executeScript("player.increaseVolume(0.1);");
                if (event.getCode() == KeyCode.DOWN)
                    engine.executeScript("player.decreaseVolume(-0.1);");
                if (event.getCode() == KeyCode.SPACE)
                    engine.executeScript("player.togglePlay();");
                event.consume(); // Prevent default
            }
        });
        //Comment below HBox
        leftVBox.getChildren().remove(commentBelowHBox);
        commentTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !leftVBox.getChildren().contains(commentBelowHBox)) {
                leftVBox.getChildren().add(leftVBox.getChildren().indexOf(commentHBox) + 1, commentBelowHBox);
                commentButton.requestFocus();
            }
        });
        //Icons
        likeButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                likeIcon.setContent("M0 7H3V17H0V7ZM15.77 7H11.54L13.06 2.06C13.38 1.03 12.54 0 11.38 0C10.8 0 10.24 0.24 9.86 0.65L4 7V17H14.43C15.49 17 16.41 16.33 16.62 15.39L17.96 9.39C18.23 8.15 17.18 7 15.77 7Z");
            } else {
                likeIcon.setContent("M18.77 11H14.54L16.06 6.06C16.38 5.03 15.54 4 14.38 4C13.8 4 13.24 4.24 12.86 4.65L7 11H3V21H7H8H17.43C18.49 21 19.41 20.33 19.62 19.39L20.96 13.39C21.23 12.15 20.18 11 18.77 11ZM7 20H4V12H7V20ZM19.98 13.17L18.64 19.17C18.54 19.65 18.03 20 17.43 20H8V11.39L13.6 5.33C13.79 5.12 14.08 5 14.38 5C14.64 5 14.88 5.11 15.01 5.3C15.08 5.4 15.16 5.56 15.1 5.77L13.58 10.71L13.18 12H14.53H18.76C19.17 12 19.56 12.17 19.79 12.46C19.92 12.61 20.05 12.86 19.98 13.17Z");
            }
        });
        dislikeButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                dislikeIcon.setContent("M18,4h3v10h-3V4z M5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21c0.58,0,1.14-0.24,1.52-0.65L17,14V4H6.57 C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14z");
            } else {
                dislikeIcon.setContent("M17,4h-1H6.57C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21 c0.58,0,1.14-0.24,1.52-0.65L17,14h4V4H17z M10.4,19.67C10.21,19.88,9.92,20,9.62,20c-0.26,0-0.5-0.11-0.63-0.3 c-0.07-0.1-0.15-0.26-0.09-0.47l1.52-4.94l0.4-1.29H9.46H5.23c-0.41,0-0.8-0.17-1.03-0.46c-0.12-0.15-0.25-0.4-0.18-0.72l1.34-6 C5.46,5.35,5.97,5,6.57,5H16v8.61L10.4,19.67z M20,13h-3V5h3V13z");
            }
        });
        subsButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                subsIcon.setContent("M10 20H14C14 21.1 13.1 22 12 22C10.9 22 10 21.1 10 20ZM20 17.35V19H4V17.35L6 15.47V10.32C6 7.40001 7.56 5.10001 10 4.34001V3.96001C10 2.54001 11.49 1.46001 12.99 2.20001C13.64 2.52001 14 3.23001 14 3.96001V4.35001C16.44 5.10001 18 7.41001 18 10.33V15.48L20 17.35ZM19 17.77L17 15.89V10.42C17 7.95001 15.81 6.06001 13.87 5.32001C12.61 4.79001 11.23 4.82001 10.03 5.35001C8.15 6.11001 7 7.99001 7 10.42V15.89L5 17.77V18H19V17.77Z");
            } else {
                subsIcon.setContent("");
            }
        });
    }

    Video video;
    private HomeController homeController;

    public void setVideo(Video video, HomeController homeController) {
        this.video = video;
        this.homeController = homeController;
        titleLabel.setText(video.getName());
        authorLabel.setText(video.getChannel().getName());
        DecimalFormat formatter = new DecimalFormat("#,###");
        Long numberOfViews = YouTube.client.getViewsOfVideo(video.getVideoId());

        viewsLabel.setText(formatter.format(numberOfViews) + " views . " + DateFormats.formatTimestamp(video.getCreatedDateTime()));
        desLabel.setText(video.getDescription());

        try {
            Path path = new File("src/main/resources/Client/profile.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            authorProfile.getEngine().loadContent(htmlContent.replace("@id", video.getChannelId() + ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Channel> subscribers = YouTube.client.getChannelSubscribers(video.getChannelId());
        if(subscribers != null)
            subsLabel.setText(subscribers.size() + " subscribers ");

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
//            videoWebView.getEngine().loadContent(htmlContent.replace("@id", video.getVideoId()+""));
            engine = videoWebView.getEngine();
            engine.loadContent(htmlContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Task<Void> loaderView = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    setUpComments();
                    YouTube.client.addVideoView(new VideoView(video.getVideoId(), YouTube.client.getAccount().getChannelId()));
                    recommendedVideos = YouTube.client.searchVideo(YouTube.client.getCategoriesOfVideo(video.getVideoId()), "", 10, 1);
                    currentReaction = YouTube.client.sendVideoGetReactionRequest(YouTube.client.getAccount().getChannelId(), video.getVideoId());
                    isVideoLiked = YouTube.client.isVideoLiked(video.getVideoId());

                    initialLikeCount = YouTube.client.getLikesOfVideo(video.getVideoId());
                    setVideoLikedState();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    for (Video recVideo : recommendedVideos) {
                        if (!Objects.equals(recVideo.getVideoId(), video.getVideoId())) {
                            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
                            Parent smallVideo = null;
                            try {
                                smallVideo = fxmlLoader.load();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            SmallVideoView controller = fxmlLoader.getController();
                            controller.setVideo(recVideo, homeController);
                            sideBarFlow.getChildren().add(smallVideo);
                        }
                    }
                    isChannelSubscribed = YouTube.client.isSubscribedToChannel(video.getChannelId());
                    if (isChannelSubscribed) {
                        subsIcon.setContent("M10 20H14C14 21.1 13.1 22 12 22C10.9 22 10 21.1 10 20ZM20 17.35V19H4V17.35L6 15.47V10.32C6 7.40001 7.56 5.10001 10 4.34001V3.96001C10 2.54001 11.49 1.46001 12.99 2.20001C13.64 2.52001 14 3.23001 14 3.96001V4.35001C16.44 5.10001 18 7.41001 18 10.33V15.48L20 17.35ZM19 17.77L17 15.89V10.42C17 7.95001 15.81 6.06001 13.87 5.32001C12.61 4.79001 11.23 4.82001 10.03 5.35001C8.15 6.11001 7 7.99001 7 10.42V15.89L5 17.77V18H19V17.77Z");
                        subsButton.setText("Unsubscribe");
                    } else {
                        subsIcon.setContent("");
                        subsButton.setText("Subscribe");
                    }
                    editVideoBtn.setVisible(false);
                    if (Objects.equals(video.getChannelId(), YouTube.client.getAccount().getChannelId())) {
                        subsButton.setVisible(false);
                        editVideoBtn.setVisible(true);
                    }
                });


                return null;
            }


        };

        Thread thread = new Thread(loaderView);
        thread.setDaemon(true);
        thread.start();
    }

    public void setUpComments() {
        commentList = YouTube.client.getCommentsOfVideo(video.getVideoId());
        commentsLabel.setText(commentList.size() + " Comments");
        ////////////////////////////////////
//        for (int i = 0; i < 2; i++) {
//            try {
//                FXMLLoader fxmlLoader = new FXMLLoader(VideoViewController.class.getResource("comment-view.fxml"));
//                leftVBox.getChildren().add(fxmlLoader.load());
//                if (i == 0) {
//                    CommentViewController comment = fxmlLoader.getController();
//                    fxmlLoader = new FXMLLoader(VideoViewController.class.getResource("comment-view.fxml"));
//                    CommentViewController reply = fxmlLoader.getController();
//                    comment.addReply(fxmlLoader.load());
//                    //Can't see why has error?!
////                    reply.isOnReply = true;
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        ////////////////////////////////////
        for (Comment comment : commentList) {
            try {
                FXMLLoader commentLoader = new FXMLLoader(VideoViewController.class.getResource("comment-view.fxml"));
                leftVBox.getChildren().add(commentLoader.load());
                CommentViewController controller = commentLoader.getController();

//                //TODO TOO MUCH OVERLOAD
//                //controller.isCommentLiked = YouTube.client.isCommentLiked(comment.getCommentId());
//
//                controller.setComment(comment.getChannel().getName(),comment.getChannelId(),comment.getText(),DateFormats.toRelativeTime(comment.getCreatedDateTime()),YouTube.client.getLikesOfComment(comment.getCommentId()),video.getVideoId(), comment.getCommentId(), this);
//                //TODO: Reactions
//                //Replies
//                    List<Comment> replies = YouTube.client.getRepliesOfComment(comment.getCommentId());
//                for(Comment reply : replies) {
//                    commentLoader = new FXMLLoader(VideoViewController.class.getResource("comment-view.fxml"));
//                    controller.addReply(commentLoader.load());
//                    CommentViewController replyController = commentLoader.getController();
//                    //TODO TOO MUCH OVERLOAD
////                    replyController.isCommentLiked = YouTube.client.isCommentLiked(reply.getCommentId());
//                    replyController.setComment(reply.getChannel().getName(),reply.getChannelId(),reply.getText(),DateFormats.toRelativeTime(reply.getCreatedDateTime()),YouTube.client.getLikesOfComment(reply.getCommentId()), video.getVideoId(), reply.getCommentId(), this);
//                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.printf(comment.getText());
            //YouTube.client.getRepliesOfComment(comment.getCommentId());

        }
    }

    long changeInLike = 0;

    private void setVideoLikedState() {
        try {
            if (isVideoLiked.get(true) == 1) {
                likeIcon.setContent("M0 7H3V17H0V7ZM15.77 7H11.54L13.06 2.06C13.38 1.03 12.54 0 11.38 0C10.8 0 10.24 0.24 9.86 0.65L4 7V17H14.43C15.49 17 16.41 16.33 16.62 15.39L17.96 9.39C18.23 8.15 17.18 7 15.77 7Z");
                dislikeIcon.setContent("M17,4h-1H6.57C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21 c0.58,0,1.14-0.24,1.52-0.65L17,14h4V4H17z M10.4,19.67C10.21,19.88,9.92,20,9.62,20c-0.26,0-0.5-0.11-0.63-0.3 c-0.07-0.1-0.15-0.26-0.09-0.47l1.52-4.94l0.4-1.29H9.46H5.23c-0.41,0-0.8-0.17-1.03-0.46c-0.12-0.15-0.25-0.4-0.18-0.72l1.34-6 C5.46,5.35,5.97,5,6.57,5H16v8.61L10.4,19.67z M20,13h-3V5h3V13z");
            } else {
                dislikeIcon.setContent("M18,4h3v10h-3V4z M5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21c0.58,0,1.14-0.24,1.52-0.65L17,14V4H6.57 C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14z");
                likeIcon.setContent("M18.77 11H14.54L16.06 6.06C16.38 5.03 15.54 4 14.38 4C13.8 4 13.24 4.24 12.86 4.65L7 11H3V21H7H8H17.43C18.49 21 19.41 20.33 19.62 19.39L20.96 13.39C21.23 12.15 20.18 11 18.77 11ZM7 20H4V12H7V20ZM19.98 13.17L18.64 19.17C18.54 19.65 18.03 20 17.43 20H8V11.39L13.6 5.33C13.79 5.12 14.08 5 14.38 5C14.64 5 14.88 5.11 15.01 5.3C15.08 5.4 15.16 5.56 15.1 5.77L13.58 10.71L13.18 12H14.53H18.76C19.17 12 19.56 12.17 19.79 12.46C19.92 12.61 20.05 12.86 19.98 13.17Z");
            }
        } catch (Exception ex) {
            likeIcon.setContent("M18.77 11H14.54L16.06 6.06C16.38 5.03 15.54 4 14.38 4C13.8 4 13.24 4.24 12.86 4.65L7 11H3V21H7H8H17.43C18.49 21 19.41 20.33 19.62 19.39L20.96 13.39C21.23 12.15 20.18 11 18.77 11ZM7 20H4V12H7V20ZM19.98 13.17L18.64 19.17C18.54 19.65 18.03 20 17.43 20H8V11.39L13.6 5.33C13.79 5.12 14.08 5 14.38 5C14.64 5 14.88 5.11 15.01 5.3C15.08 5.4 15.16 5.56 15.1 5.77L13.58 10.71L13.18 12H14.53H18.76C19.17 12 19.56 12.17 19.79 12.46C19.92 12.61 20.05 12.86 19.98 13.17Z");
            dislikeIcon.setContent("M17,4h-1H6.57C5.5,4,4.59,4.67,4.38,5.61l-1.34,6C2.77,12.85,3.82,14,5.23,14h4.23l-1.52,4.94C7.62,19.97,8.46,21,9.62,21 c0.58,0,1.14-0.24,1.52-0.65L17,14h4V4H17z M10.4,19.67C10.21,19.88,9.92,20,9.62,20c-0.26,0-0.5-0.11-0.63-0.3 c-0.07-0.1-0.15-0.26-0.09-0.47l1.52-4.94l0.4-1.29H9.46H5.23c-0.41,0-0.8-0.17-1.03-0.46c-0.12-0.15-0.25-0.4-0.18-0.72l1.34-6 C5.46,5.35,5.97,5,6.57,5H16v8.61L10.4,19.67z M20,13h-3V5h3V13z");
        }
        likeButton.setText((initialLikeCount + changeInLike) + "");
        initialLikeCount = initialLikeCount + changeInLike;
        changeInLike = 0;
    }

    public void hi(ActionEvent event) {
        engine.executeScript("player.play()");
    }

    public void commentChanged(KeyEvent keyEvent) {
        commentButton.setDisable(commentTextField.getText().isBlank());
    }


    public void likeButtonAction(ActionEvent actionEvent) {
        try {
            if (isVideoLiked.get(true) == 1) {
                changeInLike = -1;
                isVideoLiked.remove(true);
                CompletableFuture.runAsync(() -> {
                    YouTube.client.sendVideoLikeDeleteRequest(currentReaction.getReactionId());
                }, executorService).thenRun(() -> currentReaction = null).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
            } else {
                changeInLike = +1;
                isVideoLiked.replace(true, (short) 1);
                currentReaction.setReactionTypeId((short) 1);
                CompletableFuture.runAsync(() -> {
                    YouTube.client.sendVideoLikeAddRequest(currentReaction);
                }, executorService).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
            }
        } catch (Exception exception) {
            changeInLike = +1;
            isVideoLiked.put(true, (short) 1);
            currentReaction = new Reaction(video.getVideoId(), YouTube.client.getAccount().getChannelId(), (short) 1);
            CompletableFuture.runAsync(() -> {
                YouTube.client.sendVideoLikeAddRequest(currentReaction);
                currentReaction = YouTube.client.sendVideoGetReactionRequest(YouTube.client.getAccount().getChannelId(), video.getVideoId());
            }, executorService).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
        setVideoLikedState();
    }

    public void dislikeButtonAction(ActionEvent actionEvent) {
        try {
            if (isVideoLiked.get(true) == 1) {
                changeInLike = -1;
                isVideoLiked.replace(true, (short) -1);
                currentReaction.setReactionTypeId((short) -1);
                CompletableFuture.runAsync(() -> {
                    YouTube.client.sendVideoLikeAddRequest(currentReaction);
                }, executorService).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
            } else {
                isVideoLiked.remove(true);
                CompletableFuture.runAsync(() -> {
                    YouTube.client.sendVideoLikeDeleteRequest(currentReaction.getReactionId());
                }, executorService).thenRun(() -> currentReaction = null).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
            }
        } catch (Exception exception) {
            isVideoLiked.put(true, (short) -1);
            currentReaction = new Reaction(video.getVideoId(), YouTube.client.getAccount().getChannelId(), (short) -1);
            CompletableFuture.runAsync(() -> {
                YouTube.client.sendVideoLikeAddRequest(currentReaction);
                currentReaction = YouTube.client.sendVideoGetReactionRequest(YouTube.client.getAccount().getChannelId(), video.getVideoId());
            }, executorService).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
        setVideoLikedState();
    }

    public void subscribeToggleAction(ActionEvent actionEvent) {
        CompletableFuture.runAsync(() -> {
            try {
                if (isChannelSubscribed) {
                    YouTube.client.sendUnsubscribeRequest(YouTube.client.getAccount().getChannelId(), video.getChannelId());
                    Platform.runLater(() -> {
                        subsIcon.setContent("");
                        subsButton.setText("Subscribe");
                    });
                } else {
                    YouTube.client.sendSubscribeRequest(YouTube.client.getAccount().getChannelId(), video.getChannelId());
                    Platform.runLater(() -> {
                        subsIcon.setContent("M10 20H14C14 21.1 13.1 22 12 22C10.9 22 10 21.1 10 20ZM20 17.35V19H4V17.35L6 15.47V10.32C6 7.40001 7.56 5.10001 10 4.34001V3.96001C10 2.54001 11.49 1.46001 12.99 2.20001C13.64 2.52001 14 3.23001 14 3.96001V4.35001C16.44 5.10001 18 7.41001 18 10.33V15.48L20 17.35ZM19 17.77L17 15.89V10.42C17 7.95001 15.81 6.06001 13.87 5.32001C12.61 4.79001 11.23 4.82001 10.03 5.35001C8.15 6.11001 7 7.99001 7 10.42V15.89L5 17.77V18H19V17.77Z");
                        subsButton.setText("Unsubscribe");
                    });
                }

                isChannelSubscribed = !isChannelSubscribed;
                int subscribersCount = YouTube.client.getChannelSubscribers(video.getChannelId()).size();
                Platform.runLater(() -> subsLabel.setText(subscribersCount + " subscribers "));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void newCommentAction(ActionEvent actionEvent) {
        if (commentTextField.getText().isBlank()) {
            return;
        }
        YouTube.client.sendCommentAddRequest(new Comment(commentTextField.getText(), video.getVideoId(), YouTube.client.getAccount().getChannelId(), null));
        commentTextField.setText("");
    }

    public void cancelCommentAction(ActionEvent actionEvent) {
        commentTextField.setText("");
        leftVBox.getChildren().remove(commentBelowHBox);
    }

    public void downloadAction(ActionEvent event) {

    }

    public void editBtnAction(ActionEvent actionEvent) {
         homeController.setVideoEditingPage(video);
    }
}
