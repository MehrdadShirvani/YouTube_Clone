package Client;

import Shared.Models.Video;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchHistoryController {
    @FXML
    public FlowPane centerFlow;
    @FXML
    public ScrollPane scrollPane;
    private HomeController homeController;
    private final int PER_PAGE = 3;
    private int pageNumber = 1;

    public void initialize() {
        // Add a listener to the vertical scroll value
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == scrollPane.getVmax()) {
                // User has scrolled to the bottom
                setVideo();
            }
        });
    }
    public void setVideo() {
        List<Video> watchHistoryVideos = YouTube.client.getWatchHistoryVideo(PER_PAGE , pageNumber);
        System.out.println(watchHistoryVideos.toString());
        List<Timestamp> watchHistoryTimestamps = YouTube.client.getWatchHistoryTimestamp(PER_PAGE , pageNumber);
        pageNumber++;


        for (Video video : watchHistoryVideos) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-watch-history-view.fxml"));
            Parent smallVideo = null;

            try {
                smallVideo = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            SmallWatchHistoryController smallWatchHistoryController = fxmlLoader.getController();
            smallWatchHistoryController.setVideo(video, homeController);
            centerFlow.getChildren().add(smallVideo);
        }
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
