package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.noxal.common.config.JSONConfigManager;
import net.noxal.common.sql.Database;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.event.EventHandlerJarvis;
import sr.will.jarvis.manager.*;
import sr.will.jarvis.service.InputReaderService;

import javax.security.auth.login.LoginException;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Jarvis {
    private static Jarvis instance;

    private JSONConfigManager configManager;
    public Config config;

    public ThreadManager threadManager;
    public InputReaderService inputReaderService;
    public CommandConsoleManager consoleManager;
    public Database database;
    public EventManager eventManager;
    public CommandManager commandManager;
    public ModuleManager moduleManager;
    private JDA jda;

    public final long startTime = new Date().getTime();
    public boolean running = true;
    public boolean debug = false;
    public int messagesReceived = 0;

    public Jarvis() {
        instance = this;

        configManager = new JSONConfigManager(this, "jarvis.json", "config", Config.class);

        threadManager = new ThreadManager(this);
        consoleManager = new CommandConsoleManager(this);
        consoleManager.registerCommands();
        inputReaderService = new InputReaderService(consoleManager);
        inputReaderService.start();

        eventManager = new EventManager(this);
        eventManager.registerHandler(new EventHandlerJarvis(this));
        commandManager = new CommandManager(this);
        commandManager.registerCommands();
        moduleManager = new ModuleManager(this);
        moduleManager.registerModules();

        database = new Database();

        reload();

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(config.discord.token)
                    .setAutoReconnect(true)
                    .addEventListener(eventManager)
                    .buildAsync();
        } catch (LoginException e) {
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
        new JarvisThread()
                .repeat(true, config.discord.statusMessageInterval * 1000)
                .name("GameThread")
                .runnable(() -> {
                    int rand = ThreadLocalRandom.current().nextInt(0, config.discord.statusMessages.size());
                    Jarvis.getJda().getPresence().setGame(Game.playing(config.discord.statusMessages.get(rand)));
                }).start();

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).finishStart()));

        System.out.println("Finished starting!");
    }

    public void stop() {
        System.out.println("Stopping!");

        running = false;

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).stop()));
        threadManager.stop();

        jda.shutdown();
        database.disconnect();

        System.exit(0);
    }

    public void reload() {
        configManager.reloadConfig();
        config = (Config) configManager.getConfig();

        debug = config.debug;

        database.setCredentials(config.sql.host, config.sql.database, config.sql.user, config.sql.password);
        database.reconnect();

        deployDatabase();

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).reload()));
    }

    public void deployDatabase() {
        System.out.println("Deploying database....");

        // Create various tables if they do not exist
        database.execute("CREATE TABLE IF NOT EXISTS modules(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "module varchar(64) NOT NULL," +
                "PRIMARY KEY (id));");

        System.out.println("Done.");
    }

    public static void debug(String message) {
        if (Jarvis.getInstance().debug) {
            System.out.println("[DEBUG] " + message);
        }
    }
}
