package sr.will.jarvis.module;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class Module {
    private ModuleDescription moduleDescription;
    private ArrayList<Permission> neededPermissions = new ArrayList<>();
    private ArrayList<Long> guildWhitelist = new ArrayList<>();
    private HashMap<Long, Boolean> guildCache = new HashMap<>();
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
        if (guildCache.get(guildId) != null) {
            return guildCache.get(guildId);
        }

        boolean enabled = Jarvis.getInstance().moduleManager.isModuleEnabled(guildId, moduleDescription.getName().toLowerCase());
        setEnabled(guildId, enabled);
        return enabled;
    }

    public void setEnabled(long guildId, boolean enabled) {
        if (guildCache.get(guildId) != null) {
            guildCache.remove(guildId);
        }

        guildCache.put(guildId, enabled);
    }

    public int cacheSize() {
        return guildCache.size();
    }

    protected void registerEventHandler(EventHandler handler) {
        Jarvis.getInstance().eventManager.registerHandler(handler);
    }

    protected void registerCommand(String commandName, Command command) {
        Jarvis.getInstance().commandManager.registerCommand(commandName, command);
    }
}
