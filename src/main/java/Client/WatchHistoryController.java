package Client;

import Shared.Models.Video;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WatchHistoryController {
    @FXML
    public FlowPane centerFlow;
    @FXML
    public ScrollPane scrollPane;
    private HomeController homeController;
    private final int PER_PAGE = 5;
    private int pageNumber = 1;
    private Date currentDate = null;

    public void initialize() {
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == scrollPane.getVmax()) {
                setVideo();
            }
        });

        setVideo();
    }
    public void setVideo() {
        List<Video> watchHistoryVideos = YouTube.client.getWatchHistoryVideo(PER_PAGE , pageNumber);
        List<Date> watchHistoryTimestamps = YouTube.client.getWatchHistoryDates(PER_PAGE , pageNumber);
        pageNumber++;


        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.ENGLISH);

        for (int i = 0 ; i < watchHistoryVideos.size() ; i++) {
            try {
                Video video = watchHistoryVideos.get(i);
                Date watchedDate = watchHistoryTimestamps.get(i);

                FXMLLoader fxmlLoader = new FXMLLoader(HomeController.class.getResource("small-watch-history-view.fxml"));
                Parent smallVideo = null;

                try {
                    smallVideo = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (currentDate == null || !compareByDay(currentDate , watchedDate)) {
                    String dateString = dateFormat.format(watchedDate);
                    Label dateLabel = new Label(dateString);
                    centerFlow.getChildren().add(dateLabel);

                    currentDate = watchedDate;
                }

                SmallWatchHistoryController smallWatchHistoryController = fxmlLoader.getController();
                smallWatchHistoryController.setVideo(video, homeController);
                centerFlow.getChildren().add(smallVideo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean compareByDay(Date date0 , Date date1) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date0);
        int year0 = calendar.get(Calendar.YEAR);
        int month0 = calendar.get(Calendar.MONTH);
        int day0 = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);

        return (year0 == year1) & (month0 == month1) & (day0 == day1);
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
