package sr.will.jarvis;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.noxal.common.config.JSONConfigManager;
import net.noxal.common.sql.Database;
import sr.will.jarvis.config.Config;
import sr.will.jarvis.event.EventHandlerJarvis;
import sr.will.jarvis.manager.CommandManager;
import sr.will.jarvis.manager.EventManager;
import sr.will.jarvis.manager.ModuleManager;
import sr.will.jarvis.service.StatusService;

import javax.security.auth.login.LoginException;
import java.util.Date;

public class Jarvis {
    private static Jarvis instance;

    private JSONConfigManager configManager;
    public Config config;

    public Database database;
    public EventManager eventManager;
    public CommandManager commandManager;
    public ModuleManager moduleManager;
    public StatusService statusService;
    private JDA jda;

    public final long startTime = new Date().getTime();
    public boolean running = true;
    public int messagesReceived = 0;

    public Jarvis() {
        instance = this;

        configManager = new JSONConfigManager(this, "jarvis.json", "config", Config.class);

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

        moduleManager.getModules().forEach((s -> moduleManager.getModule(s).finishStart()));

        System.out.println("Finished starting!");
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
        database.execute("CREATE TABLE IF NOT EXISTS mutes(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "invoker bigint(20)," +
                "duration bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS bans(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "invoker bigint(20)," +
                "duration bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS scheduled_messages(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "user bigint(20) NOT NULL," +
                "channel bigint(20) NOT NULL," +
                "time bigint(20) NOT NULL," +
                "message text NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS levels(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "xp bigint(20) NOT NULL DEFAULT 0," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS levels_ignored_channels(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "channel bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS levels_silenced_channels(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "channel bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS custom_commands(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "command varchar(255) NOT NULL," +
                "response text NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS chatterbot_channels(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "channel bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS overwatch_accounts(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "user bigint(20) NOT NULL," +
                "battletag char(20) NOT NULL," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS flairs(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "role bigint(20)," +
                "name varchar(255) NOT NULL," +
                "color char(7)," +
                "PRIMARY KEY (id));");
        database.execute("CREATE TABLE IF NOT EXISTS message_data(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "channel bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "timestamp bigint(20) NOT NULL," +
                "message_length int NOT NULL," +
                "message_length_average int NOT NULL," +
                "time_from_last bigint(20) NOT NULL," +
                "xp_gained int NOT NULL," +
                "PRIMARY KEY (id));");

        System.out.println("Done.");
    }
}
