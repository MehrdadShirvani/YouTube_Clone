package Client;

import Shared.Models.Category;
import Shared.Models.Playlist;
import Shared.Models.Video;
import Shared.Models.VideoView;
import Shared.Utils.VideoProcessor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;

public class AddEditVideoView implements Initializable {
    public TextField TxtDuration;
    public WebView profileWebView;
    public VBox VControls;
    public Button ChooseVideoBtn;
    public Button SetDefaultThumbnailBtn;
    public Button SubmitBtn;
    public Button ChooseSubtitle;
    ExecutorService executorService = Executors.newCachedThreadPool();
    public TextField TxtName;
    public TextField TxtDesc;
    public CheckBox CBIsPrivate;
    public CheckBox CBIsAgeRestricted;
    public CheckBox CBIsShort;
//    public ImageView ImgThumbnail;
    File videoFile;
    File pictureFile;
    File subtitleFile;
    int videoDuration = 0;
    private Video video;
    private HomeController homeController;
    private ChecklistViewControl checkBoxPlaylistsListView;
    private ChecklistViewControl checkboxCategoriesListView;
    public void chooseFile(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Video File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MP4 files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(filter);
        Stage dialogStage = new Stage();
        videoFile = fileChooser.showOpenDialog(dialogStage);
        setDefaultThumbnail(new ActionEvent());
    }
    public void setVideo(Video video, HomeController homeController) throws IOException {
        this.homeController = homeController;
        this.video = video;

        if(video != null)
        {
            Path path = new File("src/main/resources/Client/image-view.html").toPath();
            String htmlContent = new String(Files.readAllBytes(path));
            profileWebView.getEngine().loadContent(htmlContent.replace("@url", "http://localhost:2131/image/T_" + video.getVideoId()));
            TxtName.setText(video.getName());
            TxtDesc.setText(video.getDescription());
            TxtDuration.setText(video.getDescription() + " seconds");
            SubmitBtn.setText("Edit Details");
            SetDefaultThumbnailBtn.setVisible(false);
            ChooseVideoBtn.setVisible(false);
        }

        Task<Void> loaderView = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    //YouTube.client.getPlayistsOfVideo(YouTube.client.getAccount().getChannelId(), true);
                    List<Playlist> allPlaylists =  YouTube.client.getPlaylistsOfChannel(YouTube.client.getAccount().getChannelId(), true);
                    List<Playlist> selectedPlaylists = new ArrayList<>();
                    List<Long> selectedPlaylistIds = new ArrayList<>();
                    if(video != null)
                    {
                        selectedPlaylists = YouTube.client.getPlaylistsOfVideo(video.getVideoId());
                        for(Playlist playlist : selectedPlaylists)
                        {
                            if(playlist != null)
                            {
                                selectedPlaylistIds.add(playlist.getPlaylistId());
                            }
                        }
                    }

                    List<SelectableModelView> list = new ArrayList<>();
                    for(Playlist playlist : allPlaylists)
                    {
                        list.add(new SelectableModelView(playlist.getPlaylistId(), playlist.getName(), selectedPlaylistIds.contains(playlist.getPlaylistId())));
                    }
                    ObservableList<SelectableModelView> items = FXCollections.observableArrayList(list);
                    checkBoxPlaylistsListView.addItems(items);
                    checkBoxPlaylistsListView.setMaxHeight(30);
                    List<Category> allCategories =  YouTube.client.getCategories();
                    List<Category> selectedCategories = new ArrayList<>();
                    List<Integer> selectedCategoryIds = new ArrayList<>();
                    if(video != null)
                    {
                        selectedCategories =  YouTube.client.getCategoriesOfVideo(video.getVideoId());
                        for(Category category : selectedCategories)
                        {
                            if(category != null)
                            {
                                selectedCategoryIds.add(category.getCategoryId());
                            }
                        }
                    }
                    List<SelectableModelView> listCategories = new ArrayList<>();
                    for(Category category : allCategories)
                    {
                        listCategories.add(new SelectableModelView(category.getCategoryId(), category.getName(), selectedCategoryIds.contains(category.getCategoryId())));
                    }
                    ObservableList<SelectableModelView> itemCategories = FXCollections.observableArrayList(listCategories);
                    checkboxCategoriesListView.addItems(itemCategories);


//                    subtitleFile =
                });


                return null;
            }


        };

        Thread thread = new Thread(loaderView);
        thread.setDaemon(true);
        thread.start();
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
        if(video == null && videoFile == null)
        {
            //TODO show alert that can only select picture after selecting video
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Picture");
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

        if(video == null && videoFile == null)
        {
            //TODO Ehsan Show error
            return;
        }
        if(video == null && pictureFile == null)
        {
            //TODO Ehsan Show error
            return;
        }

        String videoName = TxtName.getText();
        String videoDescription = TxtDesc.getText();
        Long channelId = YouTube.client.getAccount().getChannelId();
        Boolean isPrivate = CBIsPrivate.isSelected();
        Boolean isAgeRestricted = CBIsAgeRestricted.isSelected();

        Long videoId;
        if(video == null)
        {
            Video addedVideo  = new Video(videoName , videoDescription , channelId , isPrivate , isAgeRestricted, videoDuration, CBIsShort.isSelected()?2:1);
            Video videoInDB = YouTube.client.addVideo(addedVideo);
            videoId = videoInDB.getVideoId();
            uploadTheVideo(videoFile, videoId);
            uploadThePicture(pictureFile, videoId);
            if(subtitleFile != null)
            {
                uploadTheSubtitle(subtitleFile, videoId);
            }
            videoId = videoInDB.getVideoId();

        }
        else
        {
            video.setName(videoName);
            video.setDescription(videoDescription);
            video.setPrivate(isPrivate);
            video.setAgeRestricted(isAgeRestricted);
            video.setVideoTypeId(CBIsShort.isSelected()?2:1);
            YouTube.client.addVideo(video);
            YouTube.client.deleteVideoPlaylists(video.getVideoId());
            YouTube.client.deleteVideoCategories(video.getVideoId());

            videoId = video.getVideoId();
            if(pictureFile != null)
            {
                uploadThePicture(pictureFile, videoId);
            }
            if(subtitleFile != null)
            {
                uploadTheSubtitle(subtitleFile, videoId);
            }
        }

        List<Long> playlistIds = new ArrayList<>();
        List<SelectableModelView> selectedPlaylists =  checkBoxPlaylistsListView.getSelectedItems();
        for(SelectableModelView playlistModelView : selectedPlaylists)
        {
            playlistIds.add(playlistModelView.getItemId());
        }
        YouTube.client.addVideoPlaylists(videoId, playlistIds);

        List<Integer> categoryIds = new ArrayList<>();
        for(SelectableModelView categoryModelView : checkboxCategoriesListView.getSelectedItems())
        {
            Integer id = Math.toIntExact(categoryModelView.getItemId());
            categoryIds.add(id);
        }
        YouTube.client.addVideoCategories(videoId, categoryIds);

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
    private void uploadTheSubtitle(File subtitleFile, Long videoId)
    {
        try {
            URL url = new URL("http://localhost:2131/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "text/vtt|" + videoId);

            try (OutputStream outputStream = connection.getOutputStream();
                 FileInputStream fileInputStream = new FileInputStream(subtitleFile)) {

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
        checkBoxPlaylistsListView = new ChecklistViewControl();
        checkboxCategoriesListView = new ChecklistViewControl();
        VControls.getChildren().add(checkBoxPlaylistsListView);
        VControls.getChildren().add(checkboxCategoriesListView);


    }

    public void chooseSubtitle(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Subtitle");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Subtitle files (*.vtt)", "*.vtt");
        fileChooser.getExtensionFilters().add(filter);
        Stage dialogStage = new Stage();
        subtitleFile = fileChooser.showOpenDialog(dialogStage);
        setDefaultThumbnail(new ActionEvent());
    }

}
