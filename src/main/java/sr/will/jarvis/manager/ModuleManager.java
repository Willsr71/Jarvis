package sr.will.jarvis.manager;

import com.google.gson.Gson;
import net.dv8tion.jda.core.Permission;
import net.noxal.common.util.Logger;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.ModuleDescription;
import sr.will.jarvis.module.PluginClassloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleManager {
    private Jarvis jarvis;
    private Gson gson = new Gson();
    private HashMap<String, Module> modules = new HashMap<>();

    public ModuleManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void registerModule(String name, Module module) {
        modules.put(name, module);
    }

    public void unregisterModule(String name) {
        modules.remove(name);
    }

    public ArrayList<String> getModules() {
        return new ArrayList<>(modules.keySet());
    }

    public Module getModule(String name) {
        return modules.get(name);
    }

    public void enableModule(long guildId, String module) {
        jarvis.database.execute("INSERT INTO modules (guild, module) VALUES (?, ?);", guildId, module.toLowerCase());
    }

    public void disableModule(long guildId, String module) {
        jarvis.database.execute("DELETE FROM modules WHERE (guild = ? AND module = ?);", guildId, module.toLowerCase());
    }

    public boolean isModuleLoaded(File file) {
        for (Module module : modules.values()) {
            if (module.getDescription().getFile().equals(file)) {
                return true;
            }
        }

        return false;
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

    public void registerModules() {
        System.out.println("Registering modules...");

        ArrayList<File> moduleFiles = getModuleFiles("modules");
        ArrayList<ModuleDescription> moduleDescriptions = new ArrayList<>();

        for (File file : moduleFiles) {
            try {
                ModuleDescription description = getModuleDescription(file);
                if (description == null) {
                    continue;
                }

                moduleDescriptions.add(description);
            } catch (Exception ignored) {

            }
        }

        for (ModuleDescription description : moduleDescriptions) {
            try {
                loadModule(description);
            } catch (Exception ignored) {

            }
        }

        System.out.println("Done.");
    }

    public void loadModule(ModuleDescription description) throws Exception {
        try {
            URLClassLoader loader = new PluginClassloader(new URL[]{
                    description.getFile().toURI().toURL()
            });
            Class<?> main = loader.loadClass(description.getMain());
            Module moduleClass = (Module) main.getDeclaredConstructor().newInstance();

            description.setClassLoader(loader);
            moduleClass.setDescription(description);
            moduleClass.initialize();
            registerModule(description.getName(), moduleClass);

            System.out.println("Loaded plugin " + description.getName() + " version " + description.getVersion() + " by " + description.getAuthor());
        } catch (Exception e) {
            System.out.println("Failed to load plugin " + description.getName());
            e.printStackTrace();
            throw e;
        }
    }

    public void unloadModule(Module module) throws Exception {
        module.stop();

        Jarvis.getInstance().commandManager.unregisterCommands(module);
        Jarvis.getInstance().eventManager.unregisterHandlers(module);

        String name = module.getDescription().getName();

        unregisterModule(name);

        try {
            module.getDescription().getClassLoader().close();

            module.setDescription(null);
            module = null;

            System.out.println("Unloaded plugin " + name);
        } catch (Exception e) {
            System.out.println("Failed to load plugin " + name);
            e.printStackTrace();
            throw e;
        }
    }

    public ArrayList<File> getModuleFiles(String folder) {
        File moduleFolder = new File(folder);
        ArrayList<File> moduleFiles = new ArrayList<>();

        if (!moduleFolder.exists()) {
            moduleFolder.mkdir();
        }

        if (!moduleFolder.isDirectory()) {
            Logger.severe("/modules must be a directory");
            return moduleFiles;
        }

        for (File file : moduleFolder.listFiles()) {
            if (!file.isFile() || !file.getName().endsWith(".jar")) {
                continue;
            }

            moduleFiles.add(file);
        }

        return moduleFiles;
    }

    public ModuleDescription getModuleDescription(File file) throws Exception {
        try (JarFile jar = new JarFile(file)) {
            JarEntry moduleDescription = jar.getJarEntry("plugin.json");
            if (moduleDescription == null) {
                System.out.println("Module must have a module.json");
                return null;
            }

            InputStreamReader reader = new InputStreamReader(jar.getInputStream(moduleDescription));
            ModuleDescription description = gson.fromJson(reader, ModuleDescription.class);

            if (description.getName() == null) {
                System.out.println("Plugin " + file.getName() + " has no name");
                return null;
            }

            if (description.getMain() == null) {
                System.out.println("Plugin " + file.getName() + " has no main");
                return null;
            }

            description.setFile(file);
            return description;
        } catch (IOException e) {
            Logger.severe("Error loading plugin from " + file.getName());
            e.printStackTrace();
            throw e;
        }
    }
}
