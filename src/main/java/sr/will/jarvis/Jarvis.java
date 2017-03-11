package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.noxal.common.util.config.JSONConfigManager;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.listener.MessageListener;
import sr.will.jarvis.listener.ReadyListener;
import sr.will.jarvis.manager.BanManager;
import sr.will.jarvis.manager.ChatterBotManager;
import sr.will.jarvis.manager.CommandManager;
import sr.will.jarvis.manager.MuteManager;
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
    private JDA jda;

    public final long startTime = new Date().getTime();
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
            jda = new JDABuilder(AccountType.BOT).setToken(config.discord.token).addListener(new ReadyListener()).buildBlocking();
            jda.setAutoReconnect(true);
            jda.addEventListener(new MessageListener(this));
            jda.getPresence().setGame(Game.of(config.discord.game));
        } catch (LoginException | RateLimitedException | InterruptedException e) {
            e.printStackTrace();
        }

        muteManager.setup();
        banManager.setup();
    }

    public void stop() {
        System.out.println("Stopping!");

        muteManager.stop();
        banManager.stop();
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
