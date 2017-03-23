package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class LevelManager {
    private Jarvis jarvis;

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

        int rand = ThreadLocalRandom.current().nextInt(15, 25);

        jarvis.database.execute("UPDATE levels SET xp = xp + ? WHERE (guild = ? AND user = ?);", rand, guildId, userId);
    }
}
