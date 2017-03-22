package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void increaseUserExperience(String guildId, String userId) {
        jarvis.database.execute("IF EXISTS (SELECT * FROM levels WHERE (guild = ? AND user = ?)) " +
                "UPDATE levels SET xp = xp + ? WHERE (guild = ? AND user = ?) " +
                "ELSE " +
                "INSERT INTO levels (guild, user) VALUES (?, ?);", guildId, userId, 1, guildId, userId, guildId, userId);

        //jarvis.database.execute("UPDATE levels SET xp = xp + ? WHERE (guild = ? AND user = ?);", 1, guildId, userId);
    }
}
