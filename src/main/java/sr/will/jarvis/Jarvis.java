package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.noxal.common.util.config.JSONConfigManager;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.listener.GuildAvailableListener;
import sr.will.jarvis.listener.MessageListener;
import sr.will.jarvis.listener.ReadyListener;
import sr.will.jarvis.manager.BanManager;
import sr.will.jarvis.manager.ChatterBotManager;
import sr.will.jarvis.manager.CommandManager;
import sr.will.jarvis.manager.MuteManager;
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
    public MuteManager muteManager;
    public BanManager banManager;
    public ChatterBotManager chatterBotManager;
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

        muteManager = new MuteManager(this);
        banManager = new BanManager(this);
        chatterBotManager = new ChatterBotManager(this);

        database = new Database(this);

        reload();

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(config.discord.token)
                    .setAutoReconnect(true)
                    .addListener(new GuildAvailableListener(this))
                    .addListener(new MessageListener(this))
                    .addListener(new ReadyListener())
                    .buildAsync();
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
    }

    public void finishStartup() {
        statusService = new StatusService(config.discord.statusMessageInterval * 1000, config.discord.statusMessages);
        statusService.start();

        muteManager.setupAll();
        banManager.setup();
    }

    public void stop() {
        System.out.println("Stopping!");

        running = false;

        muteManager.stop();
        banManager.stop();
        statusService.interrupt();
        jda.shutdown();
        database.disconnect();

        System.exit(0);
    }

    public void reload() {
        configManager.reloadConfig();
        config = (Config) configManager.getConfig();

        database.reconnect();
    }

    public static Jarvis getInstance() {
        return instance;
    }

    public JDA getJda() {
        return jda;
    }
}
