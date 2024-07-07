package Client;

import Shared.Models.Playlist;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.Objects;

public class AddEditPlaylistController {
    public TextField TxtName;
    public CheckBox CBIsPrivate;
    public CheckBox CBIsCollaborative;
    public Button SubmitBtn;
    private Playlist playlist;

    public void setPlaylist(Playlist playlist, HomeController controller)
    {
        this.playlist = playlist;
        if(playlist != null) {
            TxtName.setText(playlist.getName());
            //Normal 1
            //Collaborative 2
            //Watch Later
            CBIsPrivate.setSelected(playlist.getPrivate());
            CBIsCollaborative.setSelected(playlist.getPlaylistTypeId() == 2);
            CBIsCollaborative.setDisable(playlist.getPrivate());
        }
    }
    public void submitAction(ActionEvent actionEvent)
    {
        if(TxtName.getText().isBlank())
        {
            //TODO Ehsan show error
            return;
        }
        if(Objects.equals(TxtName.getText(), "Watch Later"))
        {
            //TODO Ehsan show error
            return;
        }

        if(playlist == null)
        {
           Playlist newPlaylist =  YouTube.client.addPlaylist(new Playlist(TxtName.getText(),CBIsCollaborative.isSelected()?(short)2:(short)1, CBIsPrivate.isSelected()));
           YouTube.client.addChannelPlaylist(YouTube.client.getAccount().getChannelId(),newPlaylist.getPlaylistId());
            //TODO Ehsan -> get back to page
        }
        else
        {
            playlist.setName(TxtName.getText());
            playlist.setPlaylistTypeId(CBIsCollaborative.isSelected()?(short)2:(short)1);
            playlist.setPrivate(CBIsPrivate.isSelected());
            playlist = YouTube.client.editPlaylist(playlist);
            //TODO Ehsan -> get back to page
        }

    }

    public void cbPrivateAction(ActionEvent actionEvent)
    {
        CBIsCollaborative.setDisable(CBIsPrivate.isSelected());
        if(CBIsCollaborative.isDisable())
        {
            CBIsCollaborative.setSelected(false);
        }
    }
}
