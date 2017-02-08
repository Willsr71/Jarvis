package sr.will.jarvis.manager;

import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BanManager {
    private Jarvis jarvis;

    public BanManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public long getBanDuration(String guildId, String userId) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT duration FROM bans WHERE (guild = ? AND user = ?) ORDER BY id DESC LIMIT 1;", guildId, userId);
            if (result.first()) {
                return result.getLong("duration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isBanned(String userId, String guildId) {
        return DateUtils.timestampApplies(getBanDuration(guildId, userId));
    }

    public void ban(String userId, String invokerId, String guildId) {
        ban(userId, invokerId, guildId, -1);
    }

    public void ban(String userId, String invokerId, String guildId, long duration) {
        jarvis.database.execute("INSERT INTO bans (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, invokerId, duration);
    }

    public void unban(String userId, String guildId) {
        jarvis.database.execute("INSERT INTO bans (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, null, 0);
    }
}
