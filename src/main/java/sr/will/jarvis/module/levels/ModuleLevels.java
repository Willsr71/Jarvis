package sr.will.jarvis.module.levels;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.levels.command.CommandLeaderboard;
import sr.will.jarvis.module.levels.command.CommandRank;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ModuleLevels extends Module {
    private Jarvis jarvis;

    private HashMap<Integer, Long> levels = new HashMap<>();
    private ArrayList<String> disallowedUsers = new ArrayList<>();

    public ModuleLevels(Jarvis jarvis) {
        this.jarvis = jarvis;

        jarvis.commandManager.registerCommand("leaderboard", new CommandLeaderboard(this));
        jarvis.commandManager.registerCommand("rank", new CommandRank(this));

        generateLevels(100);
    }

    @Override
    public void finishStart() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getName() {
        return "Levels";
    }

    @Override
    public String getHelpText() {
        return "Levels plugin and commands";
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return new ArrayList<>(Arrays.asList(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE
        ));
    }

    @Override
    public boolean isDefaultEnabled() {
        return false;
    }


    public void addUser(String guildId, String userId) {
        Jarvis.getDatabase().execute("INSERT INTO levels (guild, user) VALUES (?, ?);", guildId, userId);
    }

    public void resetUser(String guildId, String userId) {
        Jarvis.getDatabase().execute("UPDATE levels SET xp = 0 WHERE (guild = ? AND user = ?);", guildId, userId);
    }

    public int getUserLevel(String guildId, String userId) {
        return getLevelFromXp(getUserXp(guildId, userId));
    }

    public long getUserXp(String guildId, String userId) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT xp FROM levels WHERE (guild = ? AND user = ?);", guildId, userId);
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
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT 1 FROM levels WHERE (guild = ? AND user = ?);", guildId, userId);
            return result.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public HashMap<Long, ArrayList<String>> getLeaderboard(String guildId) {
        HashMap<Long, ArrayList<String>> leaderboard = new HashMap<>();
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT user, xp FROM levels WHERE (guild = ?) ORDER BY xp DESC;", guildId);
            while (result.next()) {
                System.out.println(result.getString("user") + " = " + result.getLong("xp"));
                if (!leaderboard.containsKey(result.getLong("xp"))) {
                    leaderboard.put(result.getLong("xp"), new ArrayList<>(Collections.singletonList(result.getString("user"))));
                } else {
                    leaderboard.get(result.getLong("xp")).add(result.getString("user"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaderboard;
    }

    public int getLeaderboardPosition(String guildId, String userId) {
        HashMap<Long, ArrayList<String>> leaderboard = getLeaderboard(guildId);

        int pos = 1;
        for (long xp : leaderboard.keySet()) {
            if (leaderboard.get(xp).contains(userId)) {
                return pos;
            }
        }

        return 0;
    }

    public void increase(String guildId, String userId, TextChannel channel) {
        if (!isEnabled(channel.getGuild().getId())) {
            return;
        }

        if (!userExists(guildId, userId)) {
            addUser(guildId, userId);
        }

        if (!canGain(guildId, userId)) {
            return;
        }
        disallowGain(guildId, userId);

        long xp = getUserXp(guildId, userId);
        int rand = ThreadLocalRandom.current().nextInt(15, 25);
        Jarvis.getDatabase().execute("UPDATE levels SET xp = xp + ? WHERE (guild = ? AND user = ?);", rand, guildId, userId);

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
            long xp = ((5 * (x ^ 2)) + (50 * x) + 35) + levels.get(x - 1);
            //double level = (double) x;
            //long xp = Math.round((5.0 / 6.0) * level * (2.0 * level * level + (27.0 * level) + 91.0));
            levels.put(x, xp);
            //System.out.println(x + " = " + levels.get(x));
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

        return level;
    }
}
