package Client;

import Shared.Models.Playlist;
import Shared.Models.Video;
import Shared.Utils.DateFormats;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;

import javax.sound.midi.ShortMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SmallPlayListController {
    public Label titleLabel;
    public Label statusLabel;
    public Label viewsLabel;
    @FXML
    WebView webView;
    private List<Video> videos;
    private Playlist playlist;
    private HomeController homeController;

    public void playlistClicked(MouseEvent mouseEvent) {
        if(videos == null || videos.isEmpty())
        {
            //TODO ehsan show some alert
            return;
        }
        
        homeController.setVideoPage(videos.getFirst(),playlist, videos);
    }

    public void setPlayList(Playlist playlist, HomeController homeController) {
        this.playlist = playlist;
        this.homeController = homeController;
        Path path = new File("src/main/resources/Client/small-video-thumbnail.html").toPath();
        String htmlContent = null;
        try {
            videos =  YouTube.client.getVideosOfPlaylist(playlist.getPlaylistId());
            titleLabel.setText(playlist.getName());
            if(playlist.getPrivate())
            {
                statusLabel.setText("Private Playlist");
            }
            else if(playlist.getPlaylistTypeId() == 2){
                statusLabel.setText("Collaborative Playlist");
            }
            else {
                statusLabel.setText("Public Playlist");
            }

            htmlContent = new String(Files.readAllBytes(path));
            if(videos != null && !videos.isEmpty())
            {
                webView.getEngine().loadContent(htmlContent.replace("@id", videos.getFirst().getVideoId()+ ""));
            }
//            webView.getEngine().loadContent(htmlContent.replace("@id", playlist. + ""));
            int size = 0;
            if(videos != null)
            {
                size = videos.size();
            }

            viewsLabel.setText("Created " + DateFormats.toRelativeTime(playlist.getCreatedDateTime()) + " • " + size + " Videos");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
