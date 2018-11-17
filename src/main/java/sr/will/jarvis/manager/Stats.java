package sr.will.jarvis.manager;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.noxal.common.sql.Database;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.stats.Stat;
import sr.will.jarvis.stats.StatsdClient;
import sr.will.jarvis.thread.JarvisThread;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Stats {
    public static final long startTime = System.currentTimeMillis();
    private static Stats instance;

    private StatsdClient client;
    private JarvisThread thread;
    public ArrayList<Stat> stats = new ArrayList<>();

    public Stats() {
        instance = this;
    }

    public void start() {
        try {
            Jarvis.getLogger().info("Starting metrics!");
            client = new StatsdClient(Jarvis.getInstance().config.stats.host, Jarvis.getInstance().config.stats.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addGauge("servers", () -> Jarvis.getJda().getGuilds().size());
        addGauge("players", () -> Jarvis.getJda().getUsers().size());
        addGauge("text_channels", () -> Jarvis.getJda().getTextChannels().size());
        addGauge("voice_channels", () -> Jarvis.getJda().getVoiceChannels().size());
        addGauge("threads", Thread::activeCount);

        if (!Jarvis.getInstance().config.stats.enabled) {
            return;
        }

        thread = new JarvisThread(null, this::processStats).name("Stats").repeat(true, Jarvis.getInstance().config.stats.interval * 1000).silent(true);
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.kill();
        }

        if (client != null) {
            client.flush();
        }
    }

    public void restart() {
        stop();
        start();
    }

    private void processStats() {
        if (Stats.instance.client == null) {
            return;
        }

        for (Stat stat : stats) {
            String key = Jarvis.getInstance().config.stats.prefix + "." + stat.name;
            try {
                client.gauge(key, stat.value.call());
            } catch (NullPointerException e) {
                // Nothing
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        client.flush();
    }

    public static void incrementCounter(String name) {
        if (Stats.instance.client == null) {
            return;
        }

        Stats.instance.client.increment(Jarvis.getInstance().config.stats.prefix + "." + name);
    }

    public static void addGauge(String name, Callable<Integer> value) {
        Stats.instance.stats.add(new Stat("gauge", name, value));
    }

    public static void remove(String name, String type) {
        for (Stat stat : Stats.instance.stats) {
            if (stat.type.equals(type) && stat.name.equals(name)) {
                Stats.instance.stats.remove(stat);
                return;
            }
        }
    }

    public void processEvent(Event event) {
        new JarvisThread(null, () -> {
            incrementCounter("events_counter");
            incrementCounter("events." + event.getClass().getSimpleName());

            if (event instanceof MessageReceivedEvent) {
                Message message = ((MessageReceivedEvent) event).getMessage();

                incrementCounter("messages_counter");
                incrementCounter("messages." + message.getGuild().getName());

                /*
                Jarvis.getDatabase().execute(
                        "INSERT INTO messages (guild, channel, user, timestamp, length) VALUES (?, ?, ?, ?, ?);",
                        message.getGuild().getIdLong(),
                        message.getChannel().getIdLong(),
                        message.getAuthor().getIdLong(),
                        message.getCreationTime().toInstant().toEpochMilli(),
                        message.getContentDisplay().length()
                );
                */
            }
        }).silent(true).start();
    }

    public void processQuery(PreparedStatement statement) {
        new JarvisThread(null, () -> {
            String query = Database.getStatementString(statement);

            incrementCounter("queries_counter");
            incrementCounter("queries." + query.split(" ")[0]);

            Jarvis.getLogger().debug(query);
        }).silent(true).start();
    }
}
