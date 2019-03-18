package sr.will.jarvis.modules.assistance;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.Task;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.assistance.command.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ModuleAssistance extends Module {
    private ArrayList<Thread> reminderThreads = new ArrayList<>();

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION
        );
        setDefaultEnabled(true);

        registerCommand("define", new CommandDefine(this));
        registerCommand("eval", new CommandEval(this));
        registerCommand("google", new CommandGoogle(this));
        registerCommand("regex", new CommandRegex(this));
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

        if (channel != null) channel.sendMessage(user.getAsMention() + ", " + message).queue();
    }

    public void startReminderThread(final long userId, final long channelId, final long time, final String message) {
        Task.builder(this)
                .execute(() -> remind(userId, channelId, message))
                .delay(System.currentTimeMillis() - time, TimeUnit.NANOSECONDS)
                .name("Reminder thread")
                .submit();
    }
}
