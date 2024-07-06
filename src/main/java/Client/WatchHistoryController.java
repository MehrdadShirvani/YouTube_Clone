package Client;

import Shared.Models.Video;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private final int PER_PAGE = 5;
    private int pageNumber = 1;


    public void setVideo(HomeController homeController) {
        HashMap<Video , Timestamp> watchHistoryVideos = YouTube.client.getWatchHistory(PER_PAGE , pageNumber);
        List<Map.Entry<Video , Timestamp>> watchHistoryVideosSet = new ArrayList<>(watchHistoryVideos.entrySet());
        watchHistoryVideosSet.sort(Map.Entry.comparingByValue());
        pageNumber++;

        for (Map.Entry<Video , Timestamp> videoTimestampEntry : watchHistoryVideosSet) {
            FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-watch-history-view.fxml"));
            Parent smallVideo = null;

            try {
                smallVideo = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            SmallWatchHistoryController smallWatchHistoryController = fxmlLoader.getController();
            smallWatchHistoryController.setVideo(videoTimestampEntry.getKey(), homeController);
            centerFlow.getChildren().add(smallVideo);
        }
    }
}
