package Client;

import Shared.Models.Video;
import Shared.Utils.VideoProcessor;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;

public class AddEditVideoView {
    ExecutorService executorService = Executors.newCachedThreadPool();
    public TextField TxtName;
    public TextField TxtDesc;
    public CheckBox CBIsPrivate;
    public CheckBox CBIsAgeRestricted;
    public CheckBox CBIsShort;
    public ImageView ImgThumbnail;
    File videoFile;
    File pictureFile;
    public void chooseFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Video File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MP4 files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        Stage dialogStage = new Stage();
        videoFile = fileChooser.showOpenDialog(dialogStage);

        if (videoFile != null) {
            //TODO -> setting image to show the default thumbnail
            //TODO -> setting duration
            //
        }
    }

    private void uploadTheVideo(File selectedFile) {
        executorService.submit(() -> {
            try {

                URL url = new URL("http://localhost:2131/upload");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "video/mp4|" + 1);

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
        });
    }

    public void choosePictureFile(ActionEvent actionEvent) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Video File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);
        Stage dialogStage = new Stage();
        pictureFile = fileChooser.showOpenDialog(dialogStage);
        if(pictureFile != null)
        {
            Image image = new Image(new FileInputStream(pictureFile));
            ImgThumbnail.setImage(image);
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

        Video uploadedVideo = new Video(videoName , videoDescription , channelId , isPrivate , isAgeRestricted);

        VideoProcessor videoProcessor = new VideoProcessor(videoFileAddress);

        try {
            int videoDuration = (int) videoProcessor.getVideoDuration();
            uploadedVideo.setDuration(videoDuration);

        } catch (Exception e) {
            System.out.println("Error at getVideoDuration !");
            throw new RuntimeException(e);
        }

        Video addedVideo = YouTube.client.addVideo(uploadedVideo);
        Long videoId = addedVideo.getVideoId();

        //TODO Mehrdad
        //Upload the video and files
        //handle edit mode
    }

    public void setDefaultThumbnail(ActionEvent actionEvent)
    {

    }
}
