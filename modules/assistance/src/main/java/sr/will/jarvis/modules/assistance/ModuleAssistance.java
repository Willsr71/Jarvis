package sr.will.jarvis.modules.assistance;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.assistance.command.CommandDefine;
import sr.will.jarvis.modules.assistance.command.CommandGoogle;
import sr.will.jarvis.modules.assistance.command.CommandRemindme;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class ModuleAssistance extends Module {
    private ArrayList<Thread> reminderThreads = new ArrayList<>();

    public void initialize() {
        setDescription("Assistance", "Basic assistance such as remindme, define, and google");
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION
        );
        setDefaultEnabled(true);

        registerCommand("define", new CommandDefine(this));
        registerCommand("google", new CommandGoogle(this));
        registerCommand("remindme", new CommandRemindme(this));
    }

    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS scheduled_messages(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "user bigint(20) NOT NULL," +
                "channel bigint(20) NOT NULL," +
                "time bigint(20) NOT NULL," +
                "message text NOT NULL," +
                "PRIMARY KEY (id));");

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT user, channel, time, message FROM scheduled_messages;");
            while (result.next()) {
                startReminderThread(result.getLong("user"), result.getLong("channel"), result.getLong("time"), result.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        while (reminderThreads.size() > 0) {
            reminderThreads.get(0).interrupt();
            reminderThreads.remove(0);
        }
    }

    public void reload() {

    }

    public void addReminder(long userId, long channelId, long time, String message) {
        Jarvis.getDatabase().execute("INSERT INTO scheduled_messages (user, channel, time, message) VALUES (?, ?, ?, ?);", userId, channelId, time, message);

        startReminderThread(userId, channelId, time, message);
    }

    public void removeReminder(long userId, long channelId) {
        Jarvis.getDatabase().execute("DELETE FROM scheduled_messages WHERE (user = ? AND channel = ?);", userId, channelId);
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
}
