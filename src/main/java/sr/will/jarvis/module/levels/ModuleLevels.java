package sr.will.jarvis.module.levels;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.levels.command.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ModuleLevels extends Module {
    private Jarvis jarvis;

    private HashMap<Integer, Long> levels = new HashMap<>();
    private ArrayList<String> disallowedUsers = new ArrayList<>();

    public ModuleLevels(Jarvis jarvis) {
        super(
                "Levels",
                "Levels plugin and commands",
                new ArrayList<>(Arrays.asList(
                        Permission.MESSAGE_READ,
                        Permission.MESSAGE_WRITE
                )),
                false
        );
        this.jarvis = jarvis;

        jarvis.commandManager.registerCommand("importmee6", new CommandImportMee6(this));
        jarvis.commandManager.registerCommand("levels", new CommandLevels(this));
        jarvis.commandManager.registerCommand("levelsignorechannel", new CommandLevelsIgnoreChannel(this));
        jarvis.commandManager.registerCommand("levelsoptin", new CommandLevelsOptIn(this));
        jarvis.commandManager.registerCommand("levelsoptout", new CommandLevelsOptOut(this));
        jarvis.commandManager.registerCommand("levelssilencechannel", new CommandLevelsSilenceChannel(this));
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

    public void addUser(long guildId, long userId) {
        Jarvis.getDatabase().execute("INSERT INTO levels (guild, user) VALUES (?, ?);", guildId, userId);
    }

    public void addUser(long guildId, long userId, long xp) {
        Jarvis.getDatabase().execute("INSERT INTO levels (guild, user, xp) VALUES (?, ?, ?);", guildId, userId, xp);
    }

    public void setUserXp(long guildId, long userId, long xp) {
        Jarvis.getDatabase().execute("UPDATE levels SET xp = ? WHERE (guild = ? AND user = ?);", xp, guildId, userId);
    }

    public void resetUser(long guildId, long userId) {
        Jarvis.getDatabase().execute("UPDATE levels SET xp = 0 WHERE (guild = ? AND user = ?);", guildId, userId);
    }

    public XPUser getXPUser(long guildId, long userId) {
        HashMap<Integer, XPUser> leaderboard = getLeaderboard(guildId);

        for (int pos : leaderboard.keySet()) {
            XPUser user = leaderboard.get(pos);

            if (user.userId == userId) {
                return user;
            }
        }

        return null;
    }

    public long getUserXp(long guildId, long userId) {
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

    public boolean userExists(long guildId, long userId) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT 1 FROM levels WHERE (guild = ? AND user = ?);", guildId, userId);
            return result.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public HashMap<Integer, XPUser> getLeaderboard(long guildId) {
        HashMap<Integer, XPUser> leaderboard = new HashMap<>();

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT user, xp, (SELECT COUNT(id) FROM levels WHERE (guild = ?)) AS pos_total FROM levels WHERE (guild = ? AND xp != -1) ORDER BY xp DESC;", guildId, guildId);

            int pos = 1;
            while (result.next()) {
                leaderboard.put(pos, new XPUser(guildId, result.getLong("user"), result.getLong("xp"), pos, result.getInt("pos_total")));
                pos += 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaderboard;
    }

    public void ignoreChannel(long channelId) {
        Jarvis.getDatabase().execute("INSERT INTO levels_ignored_channels (channel) VALUES (?);", channelId);
    }

    public void unignoreChannel(long channelId) {
        Jarvis.getDatabase().execute("DELETE FROM levels_ignored_channels WHERE (channel = ?);", channelId);
    }

    public boolean channelIgnored(long channelId) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT 1 FROM levels_ignored_channels WHERE (channel = ?);", channelId);
            return result.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void silenceChannel(long channelId) {
        Jarvis.getDatabase().execute("INSERT INTO levels_silenced_channels (channel) VALUES (?);", channelId);
    }

    public void unsilenceChannel(long channelId) {
        Jarvis.getDatabase().execute("DELETE FROM levels_silenced_channels WHERE (channel = ?);", channelId);
    }

    public boolean channelSilenced(long channelId) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT 1 FROM levels_silenced_channels WHERE (channel = ?);", channelId);
            return result.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void increase(long guildId, long userId, TextChannel channel) {
        if (!isEnabled(channel.getGuild().getIdLong())) {
            return;
        }

        if (channelIgnored(channel.getIdLong())) {
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

        // User is opted out
        if (xp == -1) {
            return;
        }

        int rand = ThreadLocalRandom.current().nextInt(15, 25);
        Jarvis.getDatabase().execute("UPDATE levels SET xp = xp + ? WHERE (guild = ? AND user = ?);", rand, guildId, userId);

        if (getLevelFromXp(xp + rand) > getLevelFromXp(xp) && !channelSilenced(channel.getIdLong())) {
            channel.sendMessage("Congratulations! " + channel.getJDA().getUserById(userId).getAsMention() + " has reached level " + getLevelFromXp(xp + rand)).queue();
        }
    }

    public boolean canGain(long guildId, long userid) {
        return !disallowedUsers.contains(guildId + "|" + userid);
    }

    public synchronized void allowGain(long guildId, long userId) {
        disallowedUsers.remove(guildId + "|" + userId);
    }

    public synchronized void disallowGain(long guildId, long userId) {
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

    public class XPUser {
        public long guildId;
        public long userId;
        public long xp;
        public int level;
        public int pos;
        public int pos_total;

        public XPUser(long guildId, long userId, long xp, int pos, int pos_total) {
            this.guildId = guildId;
            this.userId = userId;
            this.xp = xp;
            this.pos = pos;
            this.pos_total = pos_total;

            this.level = getLevelFromXp(xp);
        }
    }
}
