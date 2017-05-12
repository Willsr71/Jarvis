package sr.will.jarvis.module.admin.manager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.admin.ModuleAdmin;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class BanManager {
    private ModuleAdmin module;
    private ArrayList<Thread> unbanThreads = new ArrayList<>();

    public BanManager(ModuleAdmin module) {
        this.module = module;
    }

    public HashMap<Long, Long> getBans(long guildId) {
        HashMap<Long, Long> bans = new HashMap<>();
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT user, duration FROM bans WHERE (guild = ?);", guildId);
            while (result.next()) {
                bans.put(result.getLong("user"), result.getLong("duration"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bans;
    }

    public long getBanDuration(long guildId, long userId) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT duration FROM bans WHERE (guild = ? AND user = ?) ORDER BY id DESC LIMIT 1;", guildId, userId);
            if (result.first()) {
                return result.getLong("duration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isBanned(long guildId, long userId) {
        return DateUtils.timestampApplies(getBanDuration(guildId, userId));
    }

    public void ban(long guildId, long userId, long invokerId) {
        ban(guildId, userId, invokerId, -1);
    }

    public void ban(long guildId, long userId, long invokerId, long duration) {
        Jarvis.getDatabase().execute("INSERT INTO bans (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, invokerId, duration);
        setBanned(guildId, userId, true);
        startUnbanThread(guildId, userId, duration);

        Jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> {
            privateChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Banned", "https://jarvis.will.sr")
                            .setColor(Color.RED)
                            .setDescription("You have been banned from " + Jarvis.getJda().getGuildById(guildId).getName() + " for " + DateUtils.formatDateDiff(duration))
                            .build())
                    .queue();
        }));
    }

    public void unban(long guildId, long userId) {
        Jarvis.getDatabase().execute("DELETE FROM bans WHERE (guild = ? AND user = ? );", guildId, userId);
        setBanned(guildId, userId, false);

        if (!isBanned(guildId, userId)) {
            return;
        }

        Jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> {
            privateChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Unbanned", "https://jarvis.will.sr")
                            .setColor(Color.GREEN)
                            .setDescription("You have been unbanned from " + Jarvis.getJda().getGuildById(guildId).getName())
                            .build())
                    .queue();
        }));
    }

    public void setup() {
        for (Guild guild : Jarvis.getJda().getGuilds()) {
            processBannedMembers(guild);
        }
    }

    public void stop() {
        for (Thread thread : unbanThreads) {
            thread.interrupt();
        }
    }

    public void processBannedMembers(Guild guild) {
        HashMap<Long, Long> bans = getBans(guild.getIdLong());

        System.out.println("Processing " + bans.size() + " banned members for " + guild.getName());

        for (long userId : bans.keySet()) {
            if (!DateUtils.timestampApplies(bans.get(userId))) {
                unban(guild.getIdLong(), userId);
                continue;
            }

            startUnbanThread(guild.getIdLong(), userId, bans.get(userId));
        }
    }

    public void setBanned(long guildId, long userId, boolean banned) {
        setBanned(Jarvis.getJda().getGuildById(guildId), userId, banned);
    }

    public void setBanned(Guild guild, long userId, boolean banned) {
        if (banned) {
            guild.getController().ban(String.valueOf(userId), 0).queue();
        } else {
            guild.getController().unban(String.valueOf(userId)).queue();
        }
    }

    public void startUnbanThread(final long guildId, final long userId, final long duration) {
        if (duration == -1) {
            return;
        }

        startThread(() -> {
            try {
                long sleepTime = duration - System.currentTimeMillis();
                if (sleepTime <= 0) {
                    unban(guildId, userId);
                    return;
                }

                System.out.println("Thread " + Thread.currentThread().getId() + " sleeping for " + sleepTime + "ms");
                sleep(sleepTime);
                unban(guildId, userId);
            } catch (InterruptedException e) {
                if (Jarvis.getInstance().running) {
                    e.printStackTrace();
                    startUnbanThread(guildId, userId, duration);
                }
                System.out.println("Stopping thread " + Thread.currentThread().getId() + "!");
            }
            System.out.println("Thread " + Thread.currentThread().getId() + " finished");
        });
    }

    public void startThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        unbanThreads.add(thread);
    }
}
