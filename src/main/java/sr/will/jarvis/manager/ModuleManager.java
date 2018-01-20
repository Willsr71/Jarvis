package sr.will.jarvis.manager;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.admin.ModuleAdmin;
import sr.will.jarvis.module.assistance.ModuleAssistance;
import sr.will.jarvis.module.chatbot.ModuleChatBot;
import sr.will.jarvis.module.customcommands.ModuleCustomCommands;
import sr.will.jarvis.module.flair.ModuleFlair;
import sr.will.jarvis.module.levels.ModuleLevels;
import sr.will.jarvis.module.ohno.ModuleOhNo;
import sr.will.jarvis.module.overwatch.ModuleOverwatch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleManager {
    private Jarvis jarvis;
    private HashMap<String, Module> modules = new HashMap<>();

    public ModuleManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void registerModule(String name, Module module) {
        modules.put(name, module);
    }

    public ArrayList<String> getModules() {
        return new ArrayList<>(modules.keySet());
    }

    public Module getModule(String name) {
        return modules.get(name);
    }

    public void registerModules() {
        registerModule("admin", new ModuleAdmin());
        registerModule("assistance", new ModuleAssistance());
        registerModule("chatbot", new ModuleChatBot());
        registerModule("customcommands", new ModuleCustomCommands());
        registerModule("flair", new ModuleFlair());
        registerModule("levels", new ModuleLevels());
        registerModule("ohno", new ModuleOhNo());
        registerModule("overwatch", new ModuleOverwatch());
    }

    public void enableModule(long guildId, String module) {
        jarvis.database.execute("INSERT INTO modules (guild, module) VALUES (?, ?);", guildId, module.toLowerCase());
    }

    public void disableModule(long guildId, String module) {
        jarvis.database.execute("DELETE FROM modules WHERE (guild = ? AND module = ?);", guildId, module.toLowerCase());
    }

    public boolean isModuleEnabled(long guildId, String module) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT 1 FROM modules WHERE (guild = ? AND module = ?);", guildId, module.toLowerCase());
            return (result.first());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Permission> getNeededPermissions() {
        ArrayList<Permission> permissions = new ArrayList<>();

        for (String name : modules.keySet()) {
            List<Permission> modulePerms = modules.get(name).getNeededPermissions();

            modulePerms.removeAll(permissions);
            permissions.addAll(modulePerms);
        }

        return permissions;
    }

    public void enableDefaultModules(long guildId) {
        for (String module : getDefaultModules()) {
            enableModule(guildId, module);
        }
    }

    public ArrayList<String> getDefaultModules() {
        ArrayList<String> defaultModules = new ArrayList<>();

        for (String module : getModules()) {
            if (getModule(module).isDefaultEnabled()) {
                defaultModules.add(module);
            }
        }

        return defaultModules;
    }
}
