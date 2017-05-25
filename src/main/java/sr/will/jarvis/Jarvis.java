package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.noxal.common.util.config.JSONConfigManager;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.listener.EventListener;
import sr.will.jarvis.manager.CommandManager;
import sr.will.jarvis.manager.ModuleManager;
import sr.will.jarvis.manager.ReminderManager;
import sr.will.jarvis.service.StatusService;
import sr.will.jarvis.sql.Database;

import javax.security.auth.login.LoginException;
import java.util.Date;

public class Jarvis {
    private static Jarvis instance;

    private JSONConfigManager configManager;
    public Config config;

    public Database database;
    public CommandManager commandManager;
    public ModuleManager moduleManager;
    public ReminderManager reminderManager;
    public StatusService statusService;
    private JDA jda;

    public final long startTime = new Date().getTime();
    public boolean running = true;
    public int messagesReceived = 0;

    public Jarvis() {
        instance = this;

        configManager = new JSONConfigManager(this, "jarvis.json", "config", Config.class);

        commandManager = new CommandManager(this);
        commandManager.registerCommands();
        moduleManager = new ModuleManager(this);
        moduleManager.registerModules();

        database = new Database(this);

        reload();

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(config.discord.token)
                    .setAutoReconnect(true)
                    .addEventListener(new EventListener(this))
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
    }

    public static Jarvis getInstance() {
        return instance;
    }

    public static Database getDatabase() {
        return Jarvis.getInstance().database;
    }

    public static JDA getJda() {
        return Jarvis.getInstance().jda;
    }

    public void finishStartup() {
        statusService = new StatusService(config.discord.statusMessageInterval * 1000, config.discord.statusMessages);
        statusService.start();
        reminderManager = new ReminderManager(this);
        reminderManager.setup();

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).finishStart()));
    }

    public void stop() {
        System.out.println("Stopping!");

        running = false;

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).stop()));

        statusService.interrupt();
        jda.shutdown();
        database.disconnect();

        System.exit(0);
    }

    public void reload() {
        configManager.reloadConfig();
        config = (Config) configManager.getConfig();

        database.reconnect();

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).reload()));
    }
}
