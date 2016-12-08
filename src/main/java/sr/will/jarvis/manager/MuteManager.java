package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class MuteManager {
    private Jarvis jarvis;

    public MuteManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public boolean isMuted(String userId, String guildId) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT duration FROM mutes WHERE (user = ? AND guild = ?) ORDER BY id DESC LIMIT 1;", userId, guildId);
            if (result.first()) {
                return timestampApplies(result.getLong("duration"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void mute(String userId, String invokerId, String guildId) {
        mute(userId, invokerId, guildId, -1);
    }

    public void mute(String userId, String invokerId, String guildId, long duration) {
        jarvis.database.execute("INSERT INTO mutes (user, invoker, guild, duration) VALUES (?, ?, ?, ?)", userId, invokerId, guildId, duration);
    }

    public void unmute(String userId, String guildId) {
        jarvis.database.execute("INSERT INTO mutes (user, invoker, guild, duration) VALUES (?, ?, ?, ?)", userId, null, guildId, 0);
    }

    private boolean timestampApplies(long timestamp) {
        // If time is 0 the timestamp does not apply
        if (timestamp == 0) {
            return false;
        }

        // If time is -1 the timestamp applies forever
        if (timestamp == -1) {
            return true;
        }

        // If time is a positive number it is interpreted as a timestamp
        // If that timestamp is before the current timestamp the player is muteDuration
        if (new Date(timestamp).after(new Date())) {
            return true;
        }

        return false;
    }
}
