package sr.will.jarvis.module;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;

import java.util.ArrayList;

public abstract class Module {
    public abstract void finishStart();

    public abstract void stop();

    public abstract void reload();

    public abstract String getName();

    public abstract String getHelpText();

    public abstract ArrayList<Permission> getNeededPermissions();

    public abstract boolean isDefaultEnabled();

    public boolean isEnabled(String guildId) {
        return Jarvis.getInstance().moduleManager.isModuleEnabled(guildId, getName().toLowerCase());
    }
}
