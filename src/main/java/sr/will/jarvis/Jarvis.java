package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.noxal.common.util.config.JSONConfigManager;
import sr.will.jarvis.command.*;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.listener.MessageListener;
import sr.will.jarvis.listener.ReadyListener;
import sr.will.jarvis.manager.CommandManager;
import sr.will.jarvis.manager.MuteManager;
import sr.will.jarvis.sql.Database;

import javax.security.auth.login.LoginException;

public class Jarvis {
    private static Jarvis instance;

    private JSONConfigManager configManager;
    public Config config;

    public Database database;
    public CommandManager commandManager;
    public MuteManager muteManager;
    private JDA jda;

    public Jarvis() {
        instance = this;

        configManager = new JSONConfigManager(this, "jarvis.json", "config", Config.class);

        commandManager = new CommandManager(this);
        commandManager.registerCommand("addcommand", new CommandAddCommand(this));
        commandManager.registerCommand("list", new CommandList(this));
        commandManager.registerCommand("mute", new CommandMute(this));
        commandManager.registerCommand("mutetime", new CommandMuteTime(this));
        commandManager.registerCommand("removecommand", new CommandRemoveCommand(this));
        commandManager.registerCommand("restart", new CommandRestart(this));
        commandManager.registerCommand("unmute", new CommandUnmute(this));

        muteManager = new MuteManager(this);

        database = new Database(this);

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
        System.out.println("Stopping!");

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
}
