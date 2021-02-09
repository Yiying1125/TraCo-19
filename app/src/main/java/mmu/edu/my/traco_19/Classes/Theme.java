package mmu.edu.my.traco_19.Classes;

public class Theme {
    private String name;
    private int imageSource;

    public Theme(int imageSource, String name) {
        this.name = name;
        this.imageSource = imageSource;
    }

    public String getName() {
        return name;
    }

    public int getImageSource() {
        return imageSource;
    }
}