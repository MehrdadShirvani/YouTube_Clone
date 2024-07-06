package Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class ChecklistViewControl extends VBox {
    ObservableList<String> items = FXCollections.observableArrayList(
            "Item 1", "Item 2", "Item 3", "Item 4", "Item 5",
            "Item 6", "Item 7", "Item 8", "Item 9", "Item 10"
    );
    ListView<String> listView = new ListView<>(items);

    public void setItems()
    {
        listView.setCellFactory(param -> new ListCell<>() {
            private final CheckBox checkBox;
            {
                checkBox = new CheckBox();
                setGraphic(checkBox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    checkBox.setText(item);
                    checkBox.setSelected(false);
                    setGraphic(checkBox);
                }
            }
        });

       getChildren().add(listView);
    }
    public void addItems(ObservableList<String> newItems) {
        items.addAll(newItems);
        setItems();
    }


}
