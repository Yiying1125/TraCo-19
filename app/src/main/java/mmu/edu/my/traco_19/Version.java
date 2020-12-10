package mmu.edu.my.traco_19;

public class Version {
    private String name;
    private String description;
    private int icon;

    public Version(String name, String description, int icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getIcon() {
        return icon;
    }
}

