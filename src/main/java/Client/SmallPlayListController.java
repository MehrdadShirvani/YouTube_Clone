package Client;

import Shared.Models.Playlist;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SmallPlayListController {
    @FXML
    WebView webView;

    public void playlistClicked(MouseEvent mouseEvent) {
    }

    public void setPlayList(Playlist playlist) {
        //TODO
        Path path = new File("src/main/resources/Client/small-video-thumbnail.html").toPath();
        String htmlContent = null;
        try {
            htmlContent = new String(Files.readAllBytes(path));
//            webView.getEngine().loadContent(htmlContent.replace("@id", playlist. + ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
