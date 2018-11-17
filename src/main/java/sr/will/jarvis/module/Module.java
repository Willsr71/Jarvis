package sr.will.jarvis.module;

import net.dv8tion.jda.core.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger;

    public abstract void initialize();

    public abstract void finishStart();

    public abstract void stop();

    public abstract void reload();

    public void setDescription(ModuleDescription moduleDescription) {
        this.moduleDescription = moduleDescription;

        if (moduleDescription != null) {
            this.logger = LoggerFactory.getLogger(moduleDescription.getName());
        }
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
        CachedModule m = CachedModule.getEntry(guildId, moduleDescription.getName().toLowerCase());
        if (m != null) {
            return m.moduleEnabled();
        }

        boolean enabled = Jarvis.getInstance().moduleManager.isModuleEnabled(guildId, moduleDescription.getName().toLowerCase());
        setEnabled(guildId, enabled);
        return enabled;
    }

    public void setEnabled(long guildId, boolean enabled) {
        new CachedModule(guildId, moduleDescription.getName().toLowerCase(), enabled);
    }

    protected void registerEventHandler(EventHandler handler) {
        Jarvis.getInstance().eventManager.registerHandler(handler);
    }

    protected void registerCommand(String commandName, Command command) {
        Jarvis.getInstance().commandManager.registerCommand(commandName, command);
    }

    public Logger getLogger() {
        return logger;
    }
}
