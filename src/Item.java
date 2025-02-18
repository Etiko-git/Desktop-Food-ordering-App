public class Item {
    private String name;
    private String imagePath;
    private String information;

    public Item(String name, String imagePath, String information) {
        this.name = name;
        this.imagePath = imagePath;
        this.information = information;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getInformation() {
        return information;
    }
}