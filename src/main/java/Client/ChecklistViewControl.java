package Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ChecklistViewControl extends VBox {
    ObservableList<SelectableModelView> items = FXCollections.observableArrayList();
    ListView<SelectableModelView> listView = new ListView<>(items);

    public void setItems()
    {
        listView.setCellFactory(param -> new ListCell<>() {
            private final CheckBox checkBox;
            {
                checkBox = new CheckBox();
                setGraphic(checkBox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        getItem().setSelected(isNowSelected);
                });
            }

            @Override
            protected void updateItem(SelectableModelView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    checkBox.setText(item.getName());
                    checkBox.setSelected(item.isSelected());
                    setGraphic(checkBox);
                }
            }
        });

       getChildren().add(listView);
    }
    public void addItems(ObservableList<SelectableModelView> newItems) {
        items.addAll(newItems);
        setItems();
    }
    public ObservableList<SelectableModelView> getAllSelected()
    {
        ObservableList<SelectableModelView> selectedItems = FXCollections.observableArrayList();
        for (SelectableModelView item : items) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return  selectedItems;
    }

    public List<SelectableModelView> getSelectedItems() {
        return getAllSelected();
    }
}
