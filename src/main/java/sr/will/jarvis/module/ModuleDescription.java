package sr.will.jarvis.module;

import java.io.File;

public class ModuleDescription {

    private File file;
    private String name;
    private String main;
    private String version;
    private String description;
    private String author;
    private String website;

    @Deprecated
    public ModuleDescription(String name, String main, String version, String description, String author, String website) {
        this.name = name;
        this.main = main;
        this.version = version;
        this.description = description;
        this.author = author;
        this.website = website;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getMain() {
        return main;
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
