package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.noxal.bstats.standalone.Metrics;
import net.noxal.common.config.JSONConfigManager;
import net.noxal.common.sql.Database;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.event.EventHandlerJarvis;
import sr.will.jarvis.manager.*;
import sr.will.jarvis.service.InputReaderService;
import sr.will.jarvis.thread.JarvisThread;

import javax.security.auth.login.LoginException;
import java.util.Date;
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
    public EventManager eventManager;
    public CommandManager commandManager;
    public ModuleManager moduleManager;
    private JDA jda;

    public final long startTime = new Date().getTime();
    public boolean running = true;
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

        // Metrics
        Metrics metrics = new Metrics("Jarvis", config.serverUUID, true, true, true);
        metrics.addCustomChart(new Metrics.SingleLineChart("servers", () -> Jarvis.getJda().getGuilds().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> Jarvis.getJda().getUsers().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("text_channels", () -> Jarvis.getJda().getTextChannels().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("voice_channels", () -> Jarvis.getJda().getVoiceChannels().size()));
        /*
        metrics.addCustomChart(new Metrics.AdvancedBarChart("modules", () -> {
            Map<String, int[]> map = new HashMap<>();
            try {
                ResultSet result = database.executeQuery("SELECT module, COUNT(1) FROM `modules` GROUP BY module;");
                while (result.next()) {
                    String name = result.getString("module");
                    int enabledCount = result.getInt(2);
                    int disabledCount = Jarvis.getJda().getGuilds().size() - enabledCount;
                    map.put(name, new int[]{enabledCount, disabledCount});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        }));
        */
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

        System.out.println("Finished starting!");
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

        if (config.serverUUID.equals("")) {
            config.serverUUID = UUID.randomUUID().toString();
            configManager.saveConfig();
        }

        database.setDebug(config.debug);
        database.setCredentials(config.sql.host, config.sql.database, config.sql.user, config.sql.password);
        database.reconnect();

        database.execute("CREATE TABLE IF NOT EXISTS modules(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "module varchar(64) NOT NULL," +
                "PRIMARY KEY (id));");

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).reload()));
    }

    public static void debug(String message) {
        if (Jarvis.getInstance().config.debug) {
            System.out.println("[DEBUG] " + message);
        }
    }
}
