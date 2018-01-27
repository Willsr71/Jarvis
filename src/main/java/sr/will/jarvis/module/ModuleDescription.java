package sr.will.jarvis.module;

public class ModuleDescription {

    private String name;
    private Class main;
    private String version;
    private String description;
    private String author;
    private String website;

    @Deprecated
    public ModuleDescription(String name, Class main, String version, String description, String author, String website) {
        this.name = name;
        this.main = main;
        this.version = version;
        this.description = description;
        this.author = author;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getWebsite() {
        return website;
    }
}
