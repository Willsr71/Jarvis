package sr.will.jarvis.module;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Module {
    private ModuleDescription moduleDescription;
    private ArrayList<Permission> neededPermissions = new ArrayList<>();
    private ArrayList<Long> guildWhitelist = new ArrayList<>();
    private boolean defaultEnabled = false;

    public abstract void initialize();

    public abstract void finishStart();

    public abstract void stop();

    public abstract void reload();

    public void setDescription(ModuleDescription moduleDescription) {
        this.moduleDescription = moduleDescription;
    }

    public ModuleDescription getDescription() {
        return moduleDescription;
    }

    public void setNeededPermissions(Permission... neededPermissions) {
        this.neededPermissions = new ArrayList<>(Arrays.asList(neededPermissions));
    }

    public ArrayList<Permission> getNeededPermissions() {
        return neededPermissions;
    }

    public ArrayList<Long> getGuildWhitelist() {
        return guildWhitelist;
    }

    public boolean isGuildWhitelisted(long guildId) {
        if (guildWhitelist.size() == 0) {
            return true;
        }

        return guildWhitelist.contains(guildId);
    }

    public void setGuildWhitelist(Long... guilds) {
        guildWhitelist.addAll(Arrays.asList(guilds));
    }

    public void setDefaultEnabled(boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    public boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    public boolean isEnabled(long guildId) {
        return Jarvis.getInstance().moduleManager.isModuleEnabled(guildId, moduleDescription.getName().toLowerCase());
    }

    protected void registerEventHandler(EventHandler handler) {
        Jarvis.getInstance().eventManager.registerHandler(handler);
    }

    protected void registerCommand(String commandName, Command command) {
        Jarvis.getInstance().commandManager.registerCommand(commandName, command);
    }
}
