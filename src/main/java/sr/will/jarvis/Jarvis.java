package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.noxal.common.util.config.JSONConfigManager;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.listener.GuildJoinListener;
import sr.will.jarvis.listener.MessageListener;
import sr.will.jarvis.listener.ReadyListener;
import sr.will.jarvis.manager.CommandManager;
import sr.will.jarvis.manager.ModuleManager;
import sr.will.jarvis.service.StatusService;
import sr.will.jarvis.sql.Database;

import javax.security.auth.login.LoginException;
import java.util.Date;

public class Jarvis {
    private static Jarvis instance;
    public final long startTime = new Date().getTime();
    public Config config;

    public Database database;
    public CommandManager commandManager;
    public ModuleManager moduleManager;
    public StatusService statusService;
    public boolean running = true;
    public int messagesReceived = 0;
    private JSONConfigManager configManager;
    private JDA jda;

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
                    .addEventListener(new GuildJoinListener(this))
                    .addEventListener(new MessageListener(this))
                    .addEventListener(new ReadyListener())
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

        moduleManager.getModules().forEach((s, module) -> {
            module.finishStart();
        });
    }

    public void stop() {
        System.out.println("Stopping!");

        running = false;

        moduleManager.getModules().forEach((s, module) -> {
            module.stop();
        });

        statusService.interrupt();
        jda.shutdown();
        database.disconnect();

        System.exit(0);
    }

    public void reload() {
        configManager.reloadConfig();
        config = (Config) configManager.getConfig();

        database.reconnect();

        moduleManager.getModules().forEach((s, module) -> {
            module.reload();
        });
    }
}
