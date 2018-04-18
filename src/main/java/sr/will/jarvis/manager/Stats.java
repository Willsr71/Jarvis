package sr.will.jarvis.manager;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.noxal.bstats.standalone.Metrics;
import net.noxal.common.sql.Database;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.thread.JarvisThread;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stats {
    public static final long startTime = System.currentTimeMillis();
    public static long eventsProcessed = 0;
    public static long messagesProcessed = 0;
    public static long queriesProcessed = 0;

    public static void processEvent(Event event) {
        new JarvisThread(null, () -> {
            eventsProcessed += 1;
            Jarvis.getDatabase().execute(
                    "INSERT INTO events (timestamp, type) VALUES (?, ?);",
                    System.currentTimeMillis(),
                    event.getClass().getSimpleName()
            );

            if (event instanceof MessageReceivedEvent) {
                messagesProcessed += 1;
                Message message = ((MessageReceivedEvent) event).getMessage();
                Jarvis.getDatabase().execute(
                        "INSERT INTO messages (guild, channel, user, timestamp, length) VALUES (?, ?, ?, ?, ?);",
                        message.getGuild().getIdLong(),
                        message.getChannel().getIdLong(),
                        message.getAuthor().getIdLong(),
                        message.getCreationTime().toInstant().toEpochMilli(),
                        message.getContentDisplay().length()
                );
            }
        }).silent(true).start();
    }

    public static void processQuery(PreparedStatement statement) {
        new JarvisThread(null, () -> {
            queriesProcessed += 1;

            String query = Database.getStatementString(statement);
            if (query.startsWith("INSERT INTO queries")) {
                return;
            }

            Jarvis.getDatabase().execute(
                    "INSERT INTO queries (timestamp, query) VALUES (?, ?);",
                    System.currentTimeMillis(),
                    query
            );
        }).silent(true).start();
    }

    public static int getDataInTime(String field, long seconds) {
        long millis = seconds * 1000;
        long time = System.currentTimeMillis() - millis;

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT COUNT(1) FROM " + field + " WHERE timestamp > ?", time);
            result.first();
            return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void setupMetrics() {
        // Metrics
        Metrics metrics = new Metrics("Jarvis", Jarvis.getInstance().config.serverUUID, true, true, true);
        metrics.addCustomChart(new Metrics.SingleLineChart("servers", () -> Jarvis.getJda().getGuilds().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> Jarvis.getJda().getUsers().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("text_channels", () -> Jarvis.getJda().getTextChannels().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("voice_channels", () -> Jarvis.getJda().getVoiceChannels().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("events_minute", () -> getDataInTime("events", 60 * 30) / 30));
        metrics.addCustomChart(new Metrics.SingleLineChart("messages_minute", () -> getDataInTime("messages", 60 * 30) / 30));
        metrics.addCustomChart(new Metrics.SingleLineChart("queries_minute", () -> getDataInTime("queries", 60 * 30) / 30));
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
}
