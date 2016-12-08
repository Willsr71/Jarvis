package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MuteManager {
    private Jarvis jarvis;

    public MuteManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public boolean isMuted(String userId, String guildId) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT user, guild FROM mutes WHERE (user = ? AND guild = ?);", userId, guildId);
            if (result.first()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void mute(String userId, String invokerId, String guildId) {
        jarvis.database.execute("INSERT INTO mutes (user, invoker, guild) VALUES (?, ?, ?)", userId, invokerId, guildId);
    }

    public void unmute(String userId, String guildId) {
        jarvis.database.execute("DELETE FROM mutes WHERE (user = ? AND guild = ?);", userId, guildId);
    }
}
