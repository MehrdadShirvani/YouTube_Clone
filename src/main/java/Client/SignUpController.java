package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class SignUpController {
    @FXML
    DatePicker birthDatePicker;
    @FXML
    BorderPane backgroundBorderPane;
    @FXML
    GridPane backgroundGridPane;
    @FXML
    Label designLabel;
    public void initialize() {
        backgroundBorderPane.prefWidthProperty().bind(backgroundGridPane.widthProperty().multiply((double) 892 / 1007));
        backgroundBorderPane.prefHeightProperty().bind(backgroundGridPane.heightProperty().multiply((double) 455 / 641));

    }


    public void signup(ActionEvent event) {
    }
}