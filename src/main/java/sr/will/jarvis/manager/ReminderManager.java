package sr.will.jarvis.manager;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class ReminderManager {
    private Jarvis jarvis;
    private ArrayList<Thread> reminderThreads = new ArrayList<>();

    public ReminderManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void startReminders() {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT user, channel, time, message FROM scheduled_messages;");
            while (result.next()) {
                startReminderThread(result.getLong("user"), result.getLong("channel"), result.getLong("time"), result.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addReminder(long userId, long channelId, long time, String message) {
        jarvis.database.execute("INSERT INTO scheduled_messages (user, channel, time, message) VALUES (?, ?, ?, ?);", userId, channelId, time, message);

        startReminderThread(userId, channelId, time, message);
    }

    public void removeReminder(long userId, long channelId) {
        jarvis.database.execute("DELETE FROM scheduled_messages WHERE (user = ? AND channel = ?);", userId, channelId);
    }

    public void remind(long userId, long channelId, String message) {
        removeReminder(userId, channelId);

        User user = Jarvis.getJda().getUserById(userId);
        TextChannel channel = Jarvis.getJda().getTextChannelById(channelId);

        channel.sendMessage(user.getAsMention() + ", " + message).queue();
    }

    public void startReminderThread(final long userId, final long channelId, final long time, final String message) {
        if (time == -1) {
            return;
        }

        startThread(() -> {
            try {
                long sleepTime = time - System.currentTimeMillis();
                if (sleepTime <= 0) {
                    remind(userId, channelId, message);
                    return;
                }

                System.out.println("Thread " + Thread.currentThread().getId() + " sleeping for " + sleepTime + "ms");
                sleep(sleepTime);
                remind(userId, channelId, message);
            } catch (InterruptedException e) {
                if (Jarvis.getInstance().running) {
                    e.printStackTrace();
                    startReminderThread(userId, channelId, time, message);
                }
                System.out.println("Stopping thread " + Thread.currentThread().getId() + "!");
            }
            System.out.println("Thread " + Thread.currentThread().getId() + " finished");
        });
    }

    public void startThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        reminderThreads.add(thread);
    }

    public void setup() {
        startReminders();
    }

    public void stop() {
        while (reminderThreads.size() > 0) {
            reminderThreads.get(0).interrupt();
            reminderThreads.remove(0);
        }
    }
}
