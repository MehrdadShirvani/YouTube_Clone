package Client;

import Shared.Models.Playlist;
import Shared.Models.Video;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SaveToPlaylistViewController implements Initializable {
    public VBox VControls;
    ChecklistViewControl checkBoxPlaylistsListView;
    private Video video;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkBoxPlaylistsListView = new ChecklistViewControl();
        VControls.getChildren().add(checkBoxPlaylistsListView);
    }

    public void setVideo(Video video)
    {
        this.video = video;
        List<Playlist> allPlaylists =  YouTube.client.getPublicPlaylistsForUser(YouTube.client.getAccount().getChannelId());
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

    }

    public void save()
    {
        YouTube.client.deleteVideoPlaylists(video.getVideoId());

        List<Long> playlistIds = new ArrayList<>();
        List<SelectableModelView> selectedPlaylists =  checkBoxPlaylistsListView.getSelectedItems();
        for(SelectableModelView playlistModelView : selectedPlaylists)
        {
            playlistIds.add(playlistModelView.getItemId());
        }
        YouTube.client.addVideoPlaylists(video.getVideoId(), playlistIds);
    }
}
