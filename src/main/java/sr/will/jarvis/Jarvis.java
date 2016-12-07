package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.noxal.common.util.config.JSONConfigManager;
import sr.will.jarvis.command.CommandHandler;
import sr.will.jarvis.command.CommandMute;
import sr.will.jarvis.command.CommandRestart;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.config.ControlGroups;
import sr.will.jarvis.config.Mutes;
import sr.will.jarvis.listener.MessageListener;
import sr.will.jarvis.listener.ReadyListener;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class Jarvis {
    private static Jarvis instance;

    private JSONConfigManager configManager;
    public Config config;

    public CommandHandler commandHandler;
    private JDA jda;

    private ArrayList<ControlGroups.ControlGroup> controlGroups = new ArrayList<>();
    private ArrayList<Mutes.Mute> mutes = new ArrayList<>();

    public Jarvis() {
        instance = this;

        configManager = new JSONConfigManager(this, "jarvis.json", "config", Config.class);

        commandHandler = new CommandHandler();
        commandHandler.registerCommand("mute", new CommandMute(this));
        commandHandler.registerCommand("restart", new CommandRestart(this));

        reload();

        try {
            jda = new JDABuilder(AccountType.BOT).setToken(config.discord.token).addListener(new ReadyListener()).buildAsync();
            jda.setAutoReconnect(true);
            jda.addEventListener(new MessageListener(this));
            jda.getPresence().setGame(Game.of(config.discord.game));
        } catch (LoginException | RateLimitedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        jda.shutdown();
        System.exit(0);
    }

    public void reload() {
        configManager.reloadConfig();
        config = (Config) configManager.getConfig();
    }

    public static Jarvis getInstance() {
        return instance;
    }
}
