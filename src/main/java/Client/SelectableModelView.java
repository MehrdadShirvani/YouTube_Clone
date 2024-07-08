package Client;

public class SelectableModelView
{
    public SelectableModelView(long itemId, String name, boolean isSelected)
    {
        this.itemId = itemId;
        this.name = name;
        this.isSelected = isSelected;
    }
    private Long itemId;
    private String name;
    private boolean isSelected;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
