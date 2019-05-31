package sr.will.jarvis.manager;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.noxal.common.Task;
import net.noxal.common.sql.Database;
import sr.will.jarvis.Jarvis;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Stats {
    public static final long startTime = System.currentTimeMillis();
    private static Stats instance;

    private StatsDClient client;
    Task task;
    public final ArrayList<Stat> stats = new ArrayList<>();

    public Stats() {
        instance = this;
    }

    public void start() {
        Jarvis.getLogger().info("Starting metrics!");
        client = new NonBlockingStatsDClient(Jarvis.getInstance().config.stats.prefix, Jarvis.getInstance().config.stats.host, Jarvis.getInstance().config.stats.port);

        addGauge("servers", () -> Jarvis.getJda().getGuilds().size());
        addGauge("players", () -> Jarvis.getJda().getUsers().size());
        addGauge("text_channels", () -> Jarvis.getJda().getTextChannels().size());
        addGauge("voice_channels", () -> Jarvis.getJda().getVoiceChannels().size());
        addGauge("threads", Thread::activeCount);

        if (!Jarvis.getInstance().config.stats.enabled) {
            return;
        }

        task = Task.builder(Jarvis.getInstance())
                .execute(this::processStats)
                .name("Stats")
                .repeat(Jarvis.getInstance().config.stats.interval, TimeUnit.SECONDS)
                .submit();
    }

    public void stop() {
        if (task != null) task.cancel();
        if (client != null) client.close();
    }

    public void restart() {
        stop();
        start();
    }

    private void processStats() {
        if (Stats.instance.client == null) {
            return;
        }

        synchronized (stats) {
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
        }

        client.close();
    }

    public static void incrementCounter(String name) {
        if (Stats.instance.client == null) {
            return;
        }

        Stats.instance.client.increment(Jarvis.getInstance().config.stats.prefix + "." + name);
    }

    public static void addGauge(String name, Callable<Integer> value) {
        synchronized (Stats.instance.stats) {
            Stats.instance.stats.add(new Stat("gauge", name, value));
        }
    }

    public static void remove(String name, String type) {
        synchronized (Stats.instance.stats) {
            Stats.instance.stats.removeIf(stat -> stat.type.equals(type) && stat.name.equals(name));

            /*
            for (Stat stat : Stats.instance.stats) {
                if (stat.type.equals(type) && stat.name.equals(name)) {
                    Stats.instance.stats.remove(stat);
                    return;
                }
            }
            */
        }
    }

    public void processEvent(Event event) {
        Task.builder(Jarvis.getInstance())
                .execute(() -> {
                    incrementCounter("events_counter");
                    incrementCounter("events." + event.getClass().getSimpleName());

                    if (event instanceof MessageReceivedEvent) {
                        Message message = ((MessageReceivedEvent) event).getMessage();

                        incrementCounter("messages_counter");
                        if (message.getChannelType() == ChannelType.TEXT) {
                            incrementCounter("messages." + message.getGuild().getIdLong());
                        }
                    }
                }).submit();
    }

    public void processQuery(PreparedStatement statement) {
        Task.builder(Jarvis.getInstance())
                .execute(() -> {
                    String query = Database.getStatementString(statement);

                    incrementCounter("queries_counter");
                    incrementCounter("queries." + query.split(" ")[0]);

                    Jarvis.getLogger().debug(query);
                }).submit();
    }

    public static class Stat {
        public String type;
        public String name;
        public Callable<Integer> value;

        public Stat(String type, String name, Callable<Integer> value) {
            this.type = type;
            this.name = name;
            this.value = value;
        }
    }
}
