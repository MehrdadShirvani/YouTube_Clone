package Client;

import Shared.Models.Playlist;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class AddEditPlaylistController {
    public TextField TxtName;
    public VBox txtVBox;
    public ToggleButton CBIsPrivate;
    public ToggleButton CBIsCollaborative;
    public Button SubmitBtn;
    private Playlist playlist;
    private Label errLabel;

    public void setPlaylist(Playlist playlist, HomeController controller) {
        this.playlist = playlist;
        if (playlist != null) {
            TxtName.setText(playlist.getName());
            //Normal 1
            //Collaborative 2
            //Watch Later
            CBIsPrivate.setSelected(playlist.getPrivate());
            CBIsCollaborative.setSelected(playlist.getPlaylistTypeId() == 2);
            CBIsCollaborative.setDisable(playlist.getPrivate());
        }
    }

    public void submitAction(ActionEvent actionEvent) {
        if (TxtName.getText().isBlank()) {
            return;
        }
        if (Objects.equals(TxtName.getText(), "Watch Later")) {
            //TODO Ehsan show error
            return;
        }

        if (playlist == null) {
            Playlist newPlaylist = YouTube.client.addPlaylist(new Playlist(TxtName.getText(), CBIsCollaborative.isSelected() ? (short) 2 : (short) 1, CBIsPrivate.isSelected()));
            YouTube.client.addChannelPlaylist(YouTube.client.getAccount().getChannelId(), newPlaylist.getPlaylistId());
            //TODO Ehsan -> get back to page
        } else {
            playlist.setName(TxtName.getText());
            playlist.setPlaylistTypeId(CBIsCollaborative.isSelected() ? (short) 2 : (short) 1);
            playlist.setPrivate(CBIsPrivate.isSelected());
            playlist = YouTube.client.editPlaylist(playlist);
            //TODO Ehsan -> get back to page
        }

    }

    public void checkValidation(KeyEvent keyEvent) {
        if (errLabel == null) {
            errLabel = new Label();
            errLabel.getStyleClass().add("err-label");
        }
        errLabel.setText("");
        if (TxtName.getText().isBlank()) {
            errLabel.setText("*Shouldn't be blank!");
            SubmitBtn.setDisable(true);
            try {
                txtVBox.getChildren().add(errLabel);
            }
            catch (Exception e)
            {}
        } else if (Objects.equals(TxtName.getText(), "Watch Later")) {
            SubmitBtn.setDisable(true);
            errLabel.setText("*Reserved name!");
            try {
                txtVBox.getChildren().add(errLabel);
            }
            catch (Exception e)
            {}
        } else {
            SubmitBtn.setDisable(false);
            try {
                txtVBox.getChildren().remove(errLabel);
            }
            catch (Exception e)
            {}
        }
    }
}
