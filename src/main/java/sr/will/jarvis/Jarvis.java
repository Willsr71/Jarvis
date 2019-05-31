package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.noxal.common.Task;
import net.noxal.common.cache.Cache;
import net.noxal.common.config.JSONConfigManager;
import net.noxal.common.sql.Database;
import net.noxal.common.sql.Query;
import net.noxal.common.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.event.EventHandlerJarvis;
import sr.will.jarvis.manager.*;
import sr.will.jarvis.service.InputReaderService;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Jarvis {
    private static Jarvis instance;

    private JSONConfigManager<Config> configManager;
    public Config config;

    public InputReaderService inputReaderService;
    public CommandConsoleManager consoleManager;
    public Database database;
    public EventManager eventManager;
    public CommandManager commandManager;
    public ModuleManager moduleManager;
    public Stats stats;
    private JDA jda;
    private static final Logger logger = LoggerFactory.getLogger("Jarvis");

    public boolean running = true;

    public static final String VERSION = "@version@";

    public Jarvis() {
        instance = this;

        stats = new Stats();
        configManager = new JSONConfigManager<>(this, "jarvis.json", "config", new Config());

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
        Query.setDatabase(database);

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
        Task.builder(this).execute(() -> {
            int rand = ThreadLocalRandom.current().nextInt(0, config.discord.statusMessages.size());
            Jarvis.getJda().getPresence().setGame(Game.playing(config.discord.statusMessages.get(rand)));
        })
                .repeat(config.discord.statusMessageInterval, TimeUnit.SECONDS)
                .submit();

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).finishStart()));

        logger.info("Finished starting Jarvis v{} in {}!", VERSION, DateUtils.formatDateDiff(Stats.startTime));
    }

    public void stop() {
        logger.info("Stopping!");

        running = false;


        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).stop()));

        jda.shutdown();
        database.disconnect();

        System.exit(0);
    }

    public void reload() {
        configManager.reloadConfig();
        config = configManager.getConfig();

        Cache.setCleanupInterval(config.cache.cleanupInterval, TimeUnit.SECONDS);
        Cache.restart();
        stats.restart();

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

    public static Logger getLogger() {
        return logger;
    }
}
