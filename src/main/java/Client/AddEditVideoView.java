package Client;

import Shared.Models.Video;
import Shared.Utils.VideoProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;

public class AddEditVideoView implements Initializable {
    public TextField TxtDuration;
    public WebView profileWebView;
    public VBox VControls;
    ExecutorService executorService = Executors.newCachedThreadPool();
    public TextField TxtName;
    public TextField TxtDesc;
    public CheckBox CBIsPrivate;
    public CheckBox CBIsAgeRestricted;
    public CheckBox CBIsShort;
//    public ImageView ImgThumbnail;
    File videoFile;
    File pictureFile;
    int videoDuration = 0;

    public void chooseFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Video File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MP4 files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        Stage dialogStage = new Stage();
        videoFile = fileChooser.showOpenDialog(dialogStage);
        setDefaultThumbnail(new ActionEvent());
    }
    public void setVideo(Video video) throws IOException {
        Path path = new File("src/main/resources/Client/image-view.html").toPath();
        String htmlContent = new String(Files.readAllBytes(path));
        profileWebView.getEngine().loadContent(htmlContent.replace("@url", "http://localhost:2131/image/T_" + video.getVideoId()));
    }
    private void uploadTheVideo(File selectedFile, Long videoId) {
            try {

                URL url = new URL("http://localhost:2131/upload");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "video/mp4|" + videoId);

                try (OutputStream outputStream = connection.getOutputStream();
                     FileInputStream fileInputStream = new FileInputStream(selectedFile)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();

                    int responseCode = connection.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                } finally {
                    connection.disconnect();
                }
            }catch (Exception ex)
            {

            }
    }
    public void choosePictureFile(ActionEvent actionEvent) throws IOException {
        if(videoFile == null)
        {
            //TODO show alert that can only select picture after selecting video
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Video File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);
        Stage dialogStage = new Stage();
        pictureFile = fileChooser.showOpenDialog(dialogStage);
        if(pictureFile != null)
        {
            Path path = new File("src/main/resources/Client/image-view.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            profileWebView.getEngine().loadContent(htmlContent.replace("@url", "file:///"  + pictureFile.getAbsolutePath()));
        }
    }

    public void uploadVideo(ActionEvent actionEvent) {
        if(TxtName.getText().isBlank() ||  TxtDesc.getText().isBlank())
        {
            //TODO Ehsan Show error
            return;
        }

        if(videoFile == null)
        {
            //TODO Ehsan Show error
            return;
        }
        if(pictureFile == null)
        {
            //TODO Ehsan Show error
            return;
        }

        String videoName = TxtName.getText();
        String videoDescription = TxtDesc.getText();
        Long channelId = YouTube.client.getAccount().getChannelId();
        Boolean isPrivate = CBIsPrivate.isSelected();
        Boolean isAgeRestricted = CBIsAgeRestricted.isSelected();
        String videoFileAddress = videoFile.getAbsolutePath();

        //TODO change this one back to normal
        Video uploadedVideo = new Video(videoName , videoDescription , 3656355L , isPrivate , isAgeRestricted, videoDuration, CBIsShort.isSelected()?2:1);

        Video addedVideo = YouTube.client.addVideo(uploadedVideo);
        Long videoId = addedVideo.getVideoId();

        //TODO Mehrdad
        uploadTheVideo(videoFile, videoId);
        uploadThePicture(pictureFile, videoId);
        //handle edit mode

        YouTube.changeScene("home-view.fxml");
    }

    private void uploadThePicture(File pictureFile, Long videoId)
    {
        try {
            URL url = new URL("http://localhost:2131/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "image/jpg|T_" + videoId);

            try (OutputStream outputStream = connection.getOutputStream();
                 FileInputStream fileInputStream = new FileInputStream(pictureFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();

                int responseCode = connection.getResponseCode();
                System.out.println("Response Code: " + responseCode);

            } finally {
                connection.disconnect();
            }
        }catch (Exception ex)
        {

        }
    }

    public void setDefaultThumbnail(ActionEvent actionEvent)
    {
        if (videoFile != null) {
            VideoProcessor videoProcessor = new VideoProcessor(videoFile.getAbsolutePath());
            try {
                videoDuration = (int) videoProcessor.getVideoDuration();
                TxtDuration.setText(videoDuration + "");
                String address = "src/main/resources/Client/tempImages/" + YouTube.client.getAccount().getChannelId() + "_temp.jpg";
                pictureFile = new File(address);
                address = pictureFile.getAbsolutePath();
                videoProcessor.extractFrame(address);
                Path path = new File("src/main/resources/Client/image-view.html").toPath();
                String htmlContent = new String(Files.readAllBytes(path));
                profileWebView.getEngine().loadContent(htmlContent.replace("@url", "file:///"  + address));
            } catch (Exception e) {
                System.out.println("Error at getVideoDuration !");
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Rectangle profileMaskRec = new Rectangle(500, 500);
        profileMaskRec.setArcHeight(50);
        profileMaskRec.setArcWidth(50);
        profileWebView.setClip(profileMaskRec);
        // load webview
        ChecklistViewControl checkBoxListView = new ChecklistViewControl();
        VControls.getChildren().add(checkBoxListView);
        ObservableList<String> items = FXCollections.observableArrayList(
                "Item 1", "Item 2", "Item 3", "Item 4", "Item 5",
                "Item 6", "Item 7", "Item 8", "Item 9", "Item 10"
        );
        checkBoxListView.addItems(items);
    }
}
