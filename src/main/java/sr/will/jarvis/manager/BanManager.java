package sr.will.jarvis.manager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class BanManager {
    private Jarvis jarvis;
    private ArrayList<Thread> unbanThreads = new ArrayList<>();

    public BanManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public HashMap<String, Long> getBans(String guildId) {
        HashMap<String, Long> bans = new HashMap<>();
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT user, duration FROM bans WHERE (guild = ?);", guildId);
            while (result.next()) {
                bans.put(result.getString("user"), result.getLong("duration"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bans;
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

    public boolean isBanned(String guildId, String userId) {
        return DateUtils.timestampApplies(getBanDuration(guildId, userId));
    }

    public void ban(String guildId, String userId, String invokerId) {
        ban(guildId, userId, invokerId, -1);
    }

    public void ban(String guildId, String userId, String invokerId, long duration) {
        jarvis.database.execute("INSERT INTO bans (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, invokerId, duration);
        setBanned(guildId, userId, true);
        startUnbanThread(guildId, userId, duration);

        jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> {
            privateChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Banned", "https://jarvis.will.sr")
                            .setColor(Color.RED)
                            .setDescription("You have been banned from " + jarvis.getJda().getGuildById(guildId).getName() + " for " + DateUtils.formatDateDiff(duration))
                            .build())
                    .queue();
        }));
    }

    public void unban(String guildId, String userId) {
        jarvis.database.execute("DELETE FROM bans WHERE (guild = ? AND user = ? );", guildId, userId);
        setBanned(guildId, userId, false);

        jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> {
            privateChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Unbanned", "https://jarvis.will.sr")
                            .setColor(Color.GREEN)
                            .setDescription("You have been unbanned from " + jarvis.getJda().getGuildById(guildId).getName())
                            .build())
                    .queue();
        }));
    }

    public void setup() {
        for (Guild guild : jarvis.getJda().getGuilds()) {
            System.out.println("Setting up guild " + guild.getName());
            processBannedMembers(guild);
        }
    }

    public void stop() {
        for (Thread thread : unbanThreads) {
            thread.interrupt();
        }
    }

    public void processBannedMembers(Guild guild) {
        HashMap<String, Long> bans = jarvis.muteManager.getMutes(guild.getId());

        System.out.println("Processing " + bans.size() + " banned members for " + guild.getName());

        for (String userId : bans.keySet()) {
            if (!DateUtils.timestampApplies(bans.get(userId))) {
                unban(guild.getId(), userId);
                continue;
            }

            startUnbanThread(guild.getId(), userId, bans.get(userId));
        }
    }

    public void setBanned(String guildId, String userId, boolean banned) {
        setBanned(jarvis.getJda().getGuildById(guildId), userId, banned);
    }

    public void setBanned(Guild guild, String userId, boolean banned) {
        if (banned) {
            guild.getController().ban(userId, 0).queue();
        } else {
            guild.getController().unban(userId).queue();
        }
    }

    public void startUnbanThread(final String guildId, final String userId, final long duration) {
        startThread(() -> {
            try {
                long sleepTime = duration - System.currentTimeMillis();
                System.out.println("Sleeping for " + sleepTime);
                sleep(sleepTime);
                Jarvis.getInstance().banManager.unban(guildId, userId);
            } catch (InterruptedException e) {
                if (Jarvis.getInstance().running) {
                    e.printStackTrace();
                    startUnbanThread(guildId, userId, duration);
                }
                System.out.println("Stopping thread!");
            }
        });
    }

    public void startThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        unbanThreads.add(thread);
    }
}
