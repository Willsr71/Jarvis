package sr.will.jarvis.manager;

import net.dv8tion.jda.core.entities.MessageChannel;
import sr.will.jarvis.Jarvis;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class LevelManager {
    private Jarvis jarvis;

    private HashMap<Integer, Long> levels = new HashMap<>();
    private ArrayList<String> disallowedUsers = new ArrayList<>();

    public LevelManager(Jarvis jarvis) {
        this.jarvis = jarvis;

        generateLevels(100);
        writeLevels(levels, "levels.txt");
    }

    public void addUser(String guildId, String userId) {
        jarvis.database.execute("INSERT INTO levels (guild, user) VALUES (?, ?);", guildId, userId);
    }

    public void resetUser(String guildId, String userId) {
        jarvis.database.execute("UPDATE levels SET xp = 0 WHERE (guild = ? AND user = ?);", guildId, userId);
    }

    public int getUserLevel(String guildId, String userId) {
        return getLevelFromXp(getUserXp(guildId, userId));
    }

    public long getUserXp(String guildId, String userId) {
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

    public void increase(String guildId, String userId, MessageChannel channel) {
        if (!userExists(guildId, userId)) {
            addUser(guildId, userId);
        }

        if (!canGain(guildId, userId)) {
            return;
        }
        disallowGain(guildId, userId);

        long xp = getUserXp(guildId, userId);
        int rand = ThreadLocalRandom.current().nextInt(15, 25);
        jarvis.database.execute("UPDATE levels SET xp = xp + ? WHERE (guild = ? AND user = ?);", rand, guildId, userId);

        if (getLevelFromXp(xp + rand) > getLevelFromXp(xp)) {
            channel.sendMessage("Congratulations! " + channel.getJDA().getUserById(userId).getAsMention() + " has reached level " + getLevelFromXp(xp + rand)).queue();
        }
    }

    public boolean canGain(String guildId, String userid) {
        return !disallowedUsers.contains(guildId + "|" + userid);
    }

    public synchronized void allowGain(String guildId, String userId) {
        disallowedUsers.remove(guildId + "|" + userId);
    }

    public synchronized void disallowGain(String guildId, String userId) {
        disallowedUsers.add(guildId + "|" + userId);

        new Thread(() -> {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException ignored) {
            }

            allowGain(guildId, userId);
        }).start();
    }

    private void generateLevels(int size) {
        int start = 0;
        while (levels.containsKey(start)) {
            start += 1;
        }

        generateLevels(start, size);
    }

    private void generateLevels(int start, int end) {
        if (start == 0) {
            levels.put(0, 0L);
            start = 1;
        }

        for (int x = start; x <= end; x += 1) {
            //long xp = (x * 100) + levels.get(x - 1);
            //long xp = ((5 * (x ^ 2)) + (50 * x) + 35) + levels.get(x - 1);
            double level = (double) x;
            long xp = Math.round((5.0 / 6.0) * level * (2.0 * level * level + (27.0 * level) + 91.0));
            levels.put(x, xp);
            System.out.println(x + " = " + levels.get(x));
        }
    }

    public long getLevelXp(int level) {
        if (!levels.containsKey(level)) {
            generateLevels(level);
        }

        return levels.get(level);
    }

    public int getLevelFromXp(long xp) {
        int level = 0;
        while (xp >= getLevelXp(level + 1)) {
            level += 1;
        }

        System.out.println(level);
        return level;
    }

    public static void writeLevels(HashMap<Integer, Long> levels, String file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Level,Experience\n");

            for (Integer level : levels.keySet()) {
                fileWriter.write(level + "," + levels.get(level) + "\n");
            }

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
