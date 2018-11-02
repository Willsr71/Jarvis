package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.noxal.common.config.JSONConfigManager;
import net.noxal.common.sql.Database;
import sr.will.jarvis.cache.Cache;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.event.EventHandlerJarvis;
import sr.will.jarvis.manager.*;
import sr.will.jarvis.service.InputReaderService;
import sr.will.jarvis.thread.JarvisThread;

import javax.security.auth.login.LoginException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Jarvis {
    private static Jarvis instance;

    private JSONConfigManager configManager;
    public Config config;

    public ThreadManager threadManager;
    public InputReaderService inputReaderService;
    public CommandConsoleManager consoleManager;
    public Database database;
    public Cache cache;
    public EventManager eventManager;
    public CommandManager commandManager;
    public ModuleManager moduleManager;
    public Stats stats;
    private JDA jda;

    public boolean running = true;

    public static final String VERSION = "@version@";

    public Jarvis() {
        instance = this;

        configManager = new JSONConfigManager(this, "jarvis.json", "config", Config.class);
        stats = new Stats();

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
        database.addHook(stats::processQuery);

        cache = new Cache();

        reload();

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(config.discord.token)
                    .setAutoReconnect(true)
                    .addEventListener(eventManager)
                    .build();
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
        new JarvisThread(null, () -> {
            int rand = ThreadLocalRandom.current().nextInt(0, config.discord.statusMessages.size());
            Jarvis.getJda().getPresence().setGame(Game.playing(config.discord.statusMessages.get(rand)));
        })
                .name("StatusChanger")
                .repeat(true, config.discord.statusMessageInterval * 1000)
                .silent(true)
                .start();

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).finishStart()));

        System.out.println("Finished starting Jarvis v" + VERSION + "!");
    }

    public void stop() {
        System.out.println("Stopping!");

        running = false;

        threadManager.stop();
        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).stop()));

        jda.shutdown();
        database.disconnect();

        System.exit(0);
    }

    public void reload() {
        configManager.reloadConfig();
        config = (Config) configManager.getConfig();

        cache.restart();
        stats.restart();

        if (config.serverUUID.equals("")) {
            config.serverUUID = UUID.randomUUID().toString();
            configManager.saveConfig();
        }

        database.setDebug(false);
        database.setCredentials(config.sql.host, config.sql.database, config.sql.user, config.sql.password);
        database.reconnect();

        database.execute("CREATE TABLE IF NOT EXISTS modules(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "module varchar(64) NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS messages(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "channel bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "timestamp bigint(20) NOT NULL," +
                "length int NOT NULL," +
                "PRIMARY KEY(id));");

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).reload()));
    }

    public static void debug(String message) {
        if (Jarvis.getInstance().config.debug) {
            System.out.println("[DEBUG] " + message);
        }
    }
}
