package Client;

import Shared.Models.Video;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;

public class WatchHistoryController {
    @FXML
    public FlowPane centerFlow;
    private final int PER_PAGE = 5;
    private int pageNumber = 1;


    public void setVideo(HomeController homeController) {
        List<Video> watchHistoryVideos = YouTube.client.getWatchHistory(PER_PAGE , pageNumber);
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
}
