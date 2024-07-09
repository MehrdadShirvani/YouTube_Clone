package Client;

import Shared.Models.Channel;
import Shared.Models.Playlist;
import Shared.Models.Video;
import Shared.Utils.QrCodeGenerator;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HomeController {
    @FXML
    ImageView logoImage;
    @FXML
    Button searchButton;
    @FXML
    TextField searchTextField;
    @FXML
    ToggleButton homeMenuButton;
    @FXML
    ToggleButton shortsMenuButton;
    @FXML
    ToggleButton subsMenuButton;
    @FXML
    ToggleButton channelMenuButton;
    @FXML
    ToggleButton historyMenuButton;
    @FXML
    VBox leftVBox;
    @FXML
    SVGPath homeIcon;
    @FXML
    SVGPath shortsIcon;
    @FXML
    SVGPath subsIcon;
    @FXML
    SVGPath channelIcon;
    @FXML
    SVGPath historyIcon;
    @FXML
    ScrollPane homeScrollPane;
    @FXML
    FlowPane homeVideosFlowPane;
    @FXML
    BorderPane mainBorderPane;
    @FXML
    ToggleGroup menuToggleGroup;
    @FXML
    WebView profileWebView;
    @FXML
    StackPane proStackPane;
    @FXML
    VBox subsVBox;
    Boolean isMinimized;
    private List<Video> currentVideos;
    private Rectangle maskProRec;
    private List<SmallVideoView> currentSmallVideos = new ArrayList<SmallVideoView>();
    private VideoViewController currentVideoViewController;
    private VBox accountVBox;
    private ArrayList<String> searchHistory;
    private int perPage = 8;
    private int pageNumber = 1;

    public void initialize() {
        searchHistory = YouTube.client.readSearchHistory();
        TextFields.bindAutoCompletion(searchTextField , searchHistory);

        homeScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == homeScrollPane.getVmax()) {
                updateHomepage();
            }
        });


        isMinimized = false;
        //Search transition
        ScaleTransition st = new ScaleTransition(Duration.millis(200), searchTextField);
        st.setToX(1.2);
        searchTextField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                st.playFromStart();
                searchTextField.setOpacity(1);
                searchButton.setOpacity(1);
            } else {
                st.stop();
                searchTextField.setScaleX(1.0);
                searchButton.setOpacity(0.7);
                searchTextField.setOpacity(0.7);
            }
        });
        searchTextField.scaleXProperty().addListener((obs, oldScale, newScale) -> {
            if (searchTextField.getScaleX() == 1) {
                searchButton.setStyle("-fx-border-color: #6C6C6C");
                searchButton.setScaleX(1);
                searchButton.setScaleY(1);
            } else {
                searchButton.setStyle("-fx-border-color: #065fd4");
            }
        });
        //Remain one selected
        menuToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                oldToggle.setSelected(true);
            }
        });
        //Mask profile with rec
        Platform.runLater(() -> {
            maskProRec = new Rectangle(profileWebView.getWidth(), profileWebView.getHeight());
            maskProRec.setArcWidth(profileWebView.getWidth());
            maskProRec.setArcHeight(profileWebView.getHeight());
            profileWebView.setClip(maskProRec);
            maskProRec.widthProperty().bind(profileWebView.widthProperty());
            maskProRec.heightProperty().bind(profileWebView.heightProperty());
            try {
                Path path = new File("src/main/resources/Client/profile.html").toPath();
                String htmlContent = new String(Files.readAllBytes(path));
                profileWebView.getEngine().loadContent(htmlContent.replace("@id", YouTube.client.getAccount().getChannelId() + ""));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //Account menu
        Popup accountPopup = new Popup();
        VBox accountVBox = new VBox();
        accountVBox.setStyle("-fx-background-color: #303030;-fx-padding: 10 0 10 0; -fx-border-width: 0; -fx-background-radius: 5");
        accountVBox.setFillWidth(true);

        Button logoutButton = new Button("Logout");
        logoutButton.getStylesheets().add(getClass().getResource("home.css").toExternalForm());
        logoutButton.getStyleClass().add("menu-toggle-button");
        accountVBox.getChildren().add(logoutButton);
        SVGPath logout = new SVGPath();
        logout.setContent("M20 3v18H8v-1h11V4H8V3h12zm-8.9 12.1.7.7 4.4-4.4L11.8 7l-.7.7 3.1 3.1H3v1h11.3l-3.2 3.3z");
        logout.setFill(Color.WHITE);
        logoutButton.setGraphic(logout);

        Button editProfile = new Button("Edit profile");
        editProfile.getStylesheets().add(getClass().getResource("home.css").toExternalForm());
        editProfile.getStyleClass().add("menu-toggle-button");
        accountVBox.getChildren().add(editProfile);
        SVGPath editPro = new SVGPath();
        editPro.setContent("M4 20h14v1H3V6h1v14zM6 3v15h15V3H6zm2.02 14c.36-2.13 1.93-4.1 5.48-4.1s5.12 1.97 5.48 4.1H8.02zM11 8.5a2.5 2.5 0 015 0 2.5 2.5 0 01-5 0zm3.21 3.43A3.507 3.507 0 0017 8.5C17 6.57 15.43 5 13.5 5S10 6.57 10 8.5c0 1.69 1.2 3.1 2.79 3.43-3.48.26-5.4 2.42-5.78 5.07H7V4h13v13h-.01c-.38-2.65-2.31-4.81-5.78-5.07z");
        editPro.setFill(Color.WHITE);
        editProfile.setGraphic(editPro);


        accountPopup.getContent().add(accountVBox);
        proStackPane.setOnMouseClicked(e -> {
            if (!accountPopup.isShowing()) accountPopup.show(YouTube.primaryStage, e.getScreenX(), e.getScreenY());
            else accountPopup.hide();
        });
        accountPopup.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                accountPopup.hide();
            }
        });
        editProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                accountPopup.hide();
                YouTube.changeScene("edit-account-view.fxml");
            }
        });
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = new File("src/main/java/Client/.cache");
                String[] entries = file.list();
                for(String entry : entries){
                    File currentFile = new File(file.getPath(),entry);
                    currentFile.delete();
                }

                accountPopup.hide();
                YouTube.client.setAccount(null);
                YouTube.changeScene("login-view.fxml");
            }
        });

        //Menu SVG
        homeMenuButton.setSelected(true);
        homeMenuButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                homeIcon.setContent("M0 7V18H6V12H10V18H16V7L8 0L0 7Z");
                setHome();
            } else {
                homeIcon.setContent("M12 4.33L19 10.45V20H15V14H9V20H5V10.45L12 4.33ZM12 3L4 10V21H10V15H14V21H20V10L12 3Z");
            }
        });
        shortsMenuButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                shortsIcon.setContent("m17.77 10.32-1.2-.5L18 9.06c1.84-.96 2.53-3.23 1.56-5.06s-3.24-2.53-5.07-1.56L6 6.94c-1.29.68-2.07 2.04-2 3.49.07 1.42.93 2.67 2.22 3.25.03.01 1.2.5 1.2.5L6 14.93c-1.83.97-2.53 3.24-1.56 5.07.97 1.83 3.24 2.53 5.07 1.56l8.5-4.5c1.29-.68 2.06-2.04 1.99-3.49-.07-1.42-.94-2.68-2.23-3.25zM10 14.65v-5.3L15 12l-5 2.65z");
                setShorts();
            } else {
                shortsIcon.setContent("M10 14.65v-5.3L15 12l-5 2.65zm7.77-4.33-1.2-.5L18 9.06c1.84-.96 2.53-3.23 1.56-5.06s-3.24-2.53-5.07-1.56L6 6.94c-1.29.68-2.07 2.04-2 3.49.07 1.42.93 2.67 2.22 3.25.03.01 1.2.5 1.2.5L6 14.93c-1.83.97-2.53 3.24-1.56 5.07.97 1.83 3.24 2.53 5.07 1.56l8.5-4.5c1.29-.68 2.06-2.04 1.99-3.49-.07-1.42-.94-2.68-2.23-3.25zm-.23 5.86-8.5 4.5c-1.34.71-3.01.2-3.72-1.14-.71-1.34-.2-3.01 1.14-3.72l2.04-1.08v-1.21l-.69-.28-1.11-.46c-.99-.41-1.65-1.35-1.7-2.41-.05-1.06.52-2.06 1.46-2.56l8.5-4.5c1.34-.71 3.01-.2 3.72 1.14.71 1.34.2 3.01-1.14 3.72L15.5 9.26v1.21l1.8.74c.99.41 1.65 1.35 1.7 2.41.05 1.06-.52 2.06-1.46 2.56z");
            }
        });
        subsMenuButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                subsIcon.setContent("M20 7H4V6h16v1zm2 2v12H2V9h20zm-7 6-5-3v6l5-3zm2-12H7v1h10V3z");
            } else {
                subsIcon.setContent("M10 18v-6l5 3-5 3zm7-15H7v1h10V3zm3 3H4v1h16V6zm2 3H2v12h20V9zM3 10h18v10H3V10z");
            }
        });
        historyMenuButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                historyIcon.setContent("M14.97 16.95 10 13.87V7h2v5.76l4.03 2.49-1.06 1.7zM12 2C8.73 2 5.8 3.44 4 5.83V3.02H2V9h6V7H5.62C7.08 5.09 9.36 4 12 4c4.41 0 8 3.59 8 8s-3.59 8-8 8-8-3.59-8-8H2c0 5.51 4.49 10 10 10s10-4.49 10-10S17.51 2 12 2z");
            } else {
                historyIcon.setContent("M14.97 16.95 10 13.87V7h2v5.76l4.03 2.49-1.06 1.7zM22 12c0 5.51-4.49 10-10 10S2 17.51 2 12h1c0 4.96 4.04 9 9 9s9-4.04 9-9-4.04-9-9-9C8.81 3 5.92 4.64 4.28 7.38c-.11.18-.22.37-.31.56L3.94 8H8v1H1.96V3h1v4.74c.04-.09.07-.17.11-.25.11-.22.23-.42.35-.63C5.22 3.86 8.51 2 12 2c5.51 0 10 4.49 10 10z");
            }
        });
        //Menu shortcuts
        Platform.runLater(() -> {
            YouTube.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN), homeMenuButton::fire);
        });
        Platform.runLater(() -> {
            YouTube.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), shortsMenuButton::fire);
        });
        Platform.runLater(() -> {
            YouTube.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), subsMenuButton::fire);
        });
        Platform.runLater(() -> {
            YouTube.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN), channelMenuButton::fire);
        });
        Platform.runLater(() -> {
            YouTube.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), historyMenuButton::fire);
        });
        Platform.runLater(() -> {
            YouTube.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.SLASH), searchTextField::requestFocus);
        });

        setHome();
    }

    private void setShorts() {
        mainBorderPane.setCenter(null);
        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("channel-view.fxml"));
        VBox videoPage = null;
        try {
            videoPage = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainBorderPane.setCenter(videoPage);
    }


    public void setVideoPage(Video video) {
        if (currentVideoViewController != null) {
            currentVideoViewController.videoWebView.getEngine().load(null);
            currentVideoViewController.commentProfile.getEngine().load(null);
            currentVideoViewController.authorProfile.getEngine().load(null);
        }
        mainBorderPane.setCenter(null);
        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("video-view.fxml"));
        BorderPane videoPage = null;
        try {
            videoPage = fxmlLoader.load();
            currentVideoViewController = fxmlLoader.getController();
            currentVideoViewController.setVideo(video, this, null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainBorderPane.setCenter(videoPage);
    }
    public void setVideoPage(Video video, Playlist playlist, List<Video> videos)
    {
        if (currentVideoViewController != null) {
            currentVideoViewController.videoWebView.getEngine().load(null);
            currentVideoViewController.commentProfile.getEngine().load(null);
            currentVideoViewController.authorProfile.getEngine().load(null);
        }

        mainBorderPane.setCenter(null);
        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("video-view.fxml"));
        BorderPane videoPage = null;
        try {
            videoPage = fxmlLoader.load();
            currentVideoViewController = fxmlLoader.getController();
            currentVideoViewController.setVideo(video, this, playlist, videos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainBorderPane.setCenter(videoPage);
    }
    private void setHome() {
        mainBorderPane.setCenter(homeScrollPane);
        homeVideosFlowPane.getChildren().clear();

        currentVideos = YouTube.client.getHomepageVideos(8 , 1);
        System.out.println("currentVideos = " + currentVideos);
        updateHomepage();
        for (SmallVideoView controller : currentSmallVideos) {
            controller.webView.getEngine().load(null);
            controller.profileWebView.getEngine().load(null);
        }
        currentSmallVideos = new ArrayList<SmallVideoView>();
        for (Video video : currentVideos) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            Parent smallVideo = null;
            try {
                smallVideo = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SmallVideoView controller = fxmlLoader.getController();
            currentSmallVideos.add(controller);

            controller.setVideo(video, this, null, null);
            homeVideosFlowPane.getChildren().add(smallVideo);
        }
        Platform.runLater(() -> {
            updateSubsList();
        });

    }

    public void updateHomepage() {
        pageNumber++;
        currentVideos = YouTube.client.getHomepageVideos(perPage, pageNumber);
        for (SmallVideoView controller : currentSmallVideos) {
            controller.webView.getEngine().load(null);
            controller.profileWebView.getEngine().load(null);
        }
        currentSmallVideos = new ArrayList<SmallVideoView>();
        for (Video video : currentVideos) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            Parent smallVideo = null;
            try {
                smallVideo = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SmallVideoView controller = fxmlLoader.getController();
            currentSmallVideos.add(controller);

            controller.setVideo(video, this, null, null);
            homeVideosFlowPane.getChildren().add(smallVideo);
        }
        Platform.runLater(() -> {
            updateSubsList();
        });
    }

    public void updateSubsList()
    {
        Platform.runLater(() ->
        {
            subsVBox.getChildren().clear();
            List<Channel> channels;
            channels = YouTube.client.getSubscriptions();
            if (!channels.isEmpty()) {
                subsVBox.setVisible(true);
            }
            for (Channel channel : channels) {
                FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("subs-view.fxml"));
                VBox mainVBox;
                try {
                    mainVBox = fxmlLoader.load();
                    ((SubsViewController)fxmlLoader.getController()).setChannel(channel);
                    subsVBox.getChildren().add(mainVBox);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    public void menuSwipe(MouseEvent mouseEvent) {

        if (!isMinimized) {
            Transition transition = new Transition() {
                {
                    setCycleDuration(Duration.seconds(0.7));
                }

                @Override
                protected void interpolate(double frac) {
                    double maxWidth = 100 + ((5 - 100) * frac);
                    leftVBox.setMaxWidth(maxWidth);
                }
            };
            transition.play();
        } else {
            Transition transition = new Transition() {
                {
                    setCycleDuration(Duration.seconds(0.7));
                }

                @Override
                protected void interpolate(double frac) {
                    double maxWidth = 5 + ((historyMenuButton.getPrefWidth() - 5) * frac);
                    leftVBox.setMaxWidth(maxWidth);
                }
            };
            transition.play();
        }
        isMinimized = !isMinimized;
    }

    public void checkLetter(KeyEvent keyEvent) {
        // slash for search
//        if (keyEvent.getCode() == KeyCode.SLASH) {
//            searchTextField.requestFocus();
//            System.out.println(homeScrollPane.getWidth() + " " + homeScrollPane.getHeight());
//        }
    }

    public void checkLetterSearch(KeyEvent keyEvent) {
        // esc for search canceling
        if (keyEvent.getCode() == KeyCode.SLASH && Objects.equals(searchTextField.getText(), "/")) {
            searchTextField.setText("");
            keyEvent.consume();
        }
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            searchTextField.setText("");
            searchButton.requestFocus();
        }
    }

    public void searchButtonAction(ActionEvent actionEvent) {
        if (!searchTextField.getText().isBlank()) {
            setSearch();
        } else {
            setHome();
        }
    }

    private void setSearch() {
        mainBorderPane.setCenter(homeScrollPane);
        homeVideosFlowPane.getChildren().clear();

        searchHistory.add(searchTextField.getText());
        YouTube.client.saveSearchHistory(searchHistory);
        TextFields.bindAutoCompletion(searchTextField , searchHistory);

        List<Video> videos = YouTube.client.searchVideo(null, searchTextField.getText(), 10, 1);
        for (Video video : videos) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-video-view.fxml"));
            Parent smallVideo = null;
            try {
                smallVideo = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SmallVideoView controller = fxmlLoader.getController();
            controller.setVideo(video, this, null, null);
            homeVideosFlowPane.getChildren().add(smallVideo);
        }
    }

    public void setChannel(Channel channel) {
        mainBorderPane.setCenter(null);
        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("channel-view.fxml"));

        VBox videoPage = null;
        try {
            videoPage = fxmlLoader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ChannelViewController controller = fxmlLoader.getController();
        try {
            controller.setChannel(channel, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainBorderPane.setCenter(videoPage);
    }


    public void watchHistoryKey(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("watch-history-view.fxml"));
        try {
            mainBorderPane.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        WatchHistoryController watchHistoryController = fxmlLoader.getController();
        watchHistoryController.setHomeController(this);
        watchHistoryController.setVideo();
    }

    public void setVideoEditingPage(Video video) {
        mainBorderPane.setCenter(null);
        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("add-edit-video-view.fxml"));

        try {
            mainBorderPane.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            AddEditVideoView controller = fxmlLoader.getController();
            controller.setVideo(video, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void yourChannelAction(ActionEvent actionEvent) {
        setChannel(YouTube.client.getChannelInfo(YouTube.client.getAccount().getChannelId()));
    }

    public void setPlaylistPage(Playlist playlist) {
        mainBorderPane.setCenter(null);
        FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("add-edit-playlist-view.fxml"));
        try {
            mainBorderPane.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AddEditPlaylistController controller = fxmlLoader.getController();
        controller.setPlaylist(playlist, this);
    }

    public void showAccount(MouseEvent mouseEvent) {
    }
}
