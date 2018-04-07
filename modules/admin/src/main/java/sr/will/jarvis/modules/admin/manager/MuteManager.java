package sr.will.jarvis.modules.admin.manager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.admin.ModuleAdmin;
import sr.will.jarvis.thread.JarvisThread;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MuteManager {
    private ModuleAdmin module;

    public MuteManager(ModuleAdmin module) {
        this.module = module;
    }

    public HashMap<Long, Long> getMutes(long guildId) {
        HashMap<Long, Long> mutes = new HashMap<>();
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT user, duration FROM mutes WHERE (guild = ?);", guildId);
            while (result.next()) {
                mutes.put(result.getLong("user"), result.getLong("duration"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mutes;
    }

    public long getMuteDuration(long guildId, long userId) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT duration FROM mutes WHERE (guild = ? AND user = ?) ORDER BY id DESC LIMIT 1;", guildId, userId);
            if (result.first()) {
                return result.getLong("duration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isMuted(long guildId, long userId) {
        return DateUtils.timestampApplies(getMuteDuration(guildId, userId));
    }

    public void mute(long guildId, long userId, long invokerId) {
        mute(guildId, userId, invokerId, -1);
    }

    public void mute(long guildId, long userId, long invokerId, long duration) {
        Jarvis.getDatabase().execute("INSERT INTO mutes (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, invokerId, duration);
        setMuted(guildId, userId, true);
        startUnmuteThread(guildId, userId, duration);

        /*
        jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> {
            privateChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Muted", null)
                            .setColor(Color.RED)
                            .setDescription("You have been muted in " + jarvis.getJda().getGuildById(guildId).getName() + " for " + DateUtils.formatDateDiff(duration))
                            .build())
                    .queue();
        }));
        */
    }

    public void unmute(long guildId, long userId) {
        Jarvis.getDatabase().execute("DELETE FROM mutes WHERE (guild = ? AND user = ? );", guildId, userId);
        setMuted(guildId, userId, false);

        if (!isMuted(guildId, userId)) {
            return;
        }

        Jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> {
            privateChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Unmuted", null)
                            .setColor(Color.GREEN)
                            .setDescription("You have been unmuted in " + Jarvis.getJda().getGuildById(guildId).getName())
                            .build())
                    .queue();
        }));
    }

    public void setupAll() {
        for (Guild guild : Jarvis.getJda().getGuilds()) {
            setup(guild);
        }
    }

    public void setup(Guild guild) {
        try {
            deleteOldRoles(guild);
            createMuteRole(guild);
        } catch (PermissionException e) {
            e.printStackTrace();
        }
    }

    public void deleteOldRoles(Guild guild) {
        List<Role> roles = new ArrayList<>();
        roles.addAll(guild.getRolesByName("Jarvis_Mute", true));
        roles.addAll(guild.getRolesByName("new role", true));
        for (Role role : roles) {
            role.delete().queue();
        }
    }

    public void createMuteRole(Guild guild) {
        if (!guild.getMember(Jarvis.getJda().getSelfUser()).hasPermission(Permission.MANAGE_ROLES)) {
            System.out.println("No permission " + Permission.MANAGE_ROLES.getName() + " in guild " + guild.getName() + ", cannot create mute role.");
            return;
        }

        guild.getController().createRole().setName("Jarvis_Mute").setColor(0x000001).setPermissions(Permission.MESSAGE_READ).queue(role -> {
            addMuteRoleToChannels(guild, role);
            processMutedMembers(guild, role);
        });
    }

    public void addMuteRoleToChannels(Guild guild, Role role) {
        List<TextChannel> channels = guild.getTextChannels();

        for (TextChannel channel : channels) {
            if (!guild.getMember(Jarvis.getJda().getSelfUser()).hasPermission(channel, Permission.MANAGE_ROLES)) {
                System.out.println("No permission " + Permission.MANAGE_PERMISSIONS.getName() + " in guild " + guild.getName() + ", channel " + channel.getName() + ", cannot add role to channel.");
                continue;
            }

            channel.createPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue();
        }
    }

    public void processNewMember(long guildId, long userId) {
        long duration = getMuteDuration(guildId, userId);
        if (DateUtils.timestampApplies(duration)) {
            setMuted(guildId, userId, true);
            startUnmuteThread(guildId, userId, duration);
        }
    }

    public void processMutedMembers(Guild guild, Role role) {
        HashMap<Long, Long> mutes = getMutes(guild.getIdLong());

        System.out.println("Processing " + mutes.size() + " muted members for " + guild.getName());

        for (long userId : mutes.keySet()) {
            if (!DateUtils.timestampApplies(mutes.get(userId))) {
                unmute(guild.getIdLong(), userId);
                continue;
            }

            setMuted(guild, guild.getMemberById(userId), role, true);
            startUnmuteThread(guild.getIdLong(), userId, mutes.get(userId));
        }
    }

    public void setMuted(long guildId, long userId, boolean applied) {
        Guild guild = Jarvis.getJda().getGuildById(guildId);

        setMuted(guild, guild.getMemberById(userId), guild.getRolesByName("Jarvis_Mute", true).get(0), applied);
    }

    public void setMuted(Guild guild, Member member, Role role, boolean applied) {
        if (member == null) {
            return;
        }

        if (applied) {
            guild.getController().addRolesToMember(member, role).queue();
        } else {
            guild.getController().removeRolesFromMember(member, role).queue();
        }
    }

    public void startUnmuteThread(final long guildId, final long userId, final long duration) {
        new JarvisThread(module, () -> unmute(guildId, userId)).executeAt(duration).name("Mute").start();
    }
}
