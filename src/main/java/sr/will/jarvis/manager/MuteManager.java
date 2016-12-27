package sr.will.jarvis.manager;

import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MuteManager {
    private Jarvis jarvis;

    public MuteManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public long getMuteDuration(String guildId, String userId) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT duration FROM mutes WHERE (guild = ? AND user = ?) ORDER BY id DESC LIMIT 1;", guildId, userId);
            if (result.first()) {
                return result.getLong("duration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isMuted(String userId, String guildId) {
        return DateUtils.timestampApplies(getMuteDuration(guildId, userId));
    }

    public void mute(String userId, String invokerId, String guildId) {
        mute(userId, invokerId, guildId, -1);
    }

    public void mute(String userId, String invokerId, String guildId, long duration) {
        jarvis.database.execute("INSERT INTO mutes (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, invokerId, duration);
    }

    public void unmute(String userId, String guildId) {
        jarvis.database.execute("INSERT INTO mutes (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, null, 0);
    }
}
