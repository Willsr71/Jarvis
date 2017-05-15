package sr.will.jarvis.module;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;

import java.util.ArrayList;

public abstract class Module {
    private String name;
    private String description;
    private ArrayList<Permission> neededPermissions;
    private boolean defaultEnabled;

    protected Module(String name, String description, ArrayList<Permission> neededPermissions, boolean defaultEnabled) {
        this.name = name;
        this.description = description;
        this.neededPermissions = neededPermissions;
        this.defaultEnabled = defaultEnabled;
    }

    public abstract void finishStart();

    public abstract void stop();

    public abstract void reload();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Permission> getNeededPermissions() {
        return neededPermissions;
    }

    public boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    public boolean isEnabled(long guildId) {
        return Jarvis.getInstance().moduleManager.isModuleEnabled(guildId, getName().toLowerCase());
    }
}
