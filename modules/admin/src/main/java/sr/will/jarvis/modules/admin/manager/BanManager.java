package sr.will.jarvis.modules.admin.manager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.noxal.common.Task;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.admin.ModuleAdmin;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BanManager {
    private ModuleAdmin module;

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
                            .setTitle("Banned", null)
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
                            .setTitle("Unbanned", null)
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

    public void processBannedMembers(Guild guild) {
        HashMap<Long, Long> bans = getBans(guild.getIdLong());

        if (bans.size() > 0) {
            module.getLogger().info("Processing {} banned members for {}", bans.size(), guild.getName());
        }

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
        Task.builder(module)
                .execute(() -> unban(guildId, userId))
                .delay(System.currentTimeMillis() - duration, TimeUnit.NANOSECONDS)
                .name("Unban thread")
                .submit();
    }
}
