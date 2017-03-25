package sr.will.jarvis.manager;

import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class LevelManager {
    private Jarvis jarvis;

    private ArrayList<User> users = new ArrayList<>();

    public LevelManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void addUser(String guildId, String userId) {
        jarvis.database.execute("INSERT INTO levels (guild, user) VALUES (?, ?);", guildId, userId);
    }

    public void resetUser(String guildId, String userId) {
        jarvis.database.execute("UPDATE levels SET xp = 0 WHERE (guild = ? AND user = ?);", guildId, userId);
    }

    public long getUserExperience(String guildId, String userId) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT xp FROM levels WHERE (guild = ? AND user = ?);", guildId, userId);
            if (result.first()) {
                return result.getLong("xp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean userExists(String guildId, String userId) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT 1 FROM levels WHERE (guild = ? AND user = ?);", guildId, userId);
            return result.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void increaseUserExperience(String guildId, String userId) {
        if (!userExists(guildId, userId)) {
            addUser(guildId, userId);
        }

        if (!canGainExperience(guildId, userId)) {
            return;
        }
        users.add(new User(guildId, userId, new Date().getTime()));

        int rand = ThreadLocalRandom.current().nextInt(15, 25);

        jarvis.database.execute("UPDATE levels SET xp = xp + ? WHERE (guild = ? AND user = ?);", rand, guildId, userId);
    }

    public boolean canGainExperience(String guildId, String userid) {
        for (User user : users) {
            if (user.guildId.equals(guildId) && user.userid.equals(userid)) {
                return DateUtils.timestampApplies(user.timestamp);
            }
        }

        return true;
    }

    class User {
        String guildId;
        String userid;
        long timestamp;

        User(String guildId, String userid, long timestamp) {
            this.guildId = guildId;
            this.userid = userid;
            this.timestamp = timestamp;
        }
    }
}
