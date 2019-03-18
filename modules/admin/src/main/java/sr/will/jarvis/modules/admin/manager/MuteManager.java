package sr.will.jarvis.modules.admin.manager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.noxal.common.Task;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.admin.CachedMute;
import sr.will.jarvis.modules.admin.ModuleAdmin;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        // If mute is cached, return cached version instead of querying the db
        CachedMute cachedMute = CachedMute.getEntry(guildId, userId);
        if (cachedMute != null) {
            return cachedMute.getDuration();
        }

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT duration FROM mutes WHERE (guild = ? AND user = ?) ORDER BY id DESC LIMIT 1;", guildId, userId);
            if (result.first()) {
                // Add result to cached mutes to avoid querying db
                new CachedMute(guildId, userId, result.getLong("duration"));
                return result.getLong("duration");
            } else {
                new CachedMute(guildId, userId, 0);
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

        new CachedMute(guildId, userId, duration);

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

        new CachedMute(guildId, userId, 0);

        setMuted(guildId, userId, false);

        if (!isMuted(guildId, userId)) {
            return;
        }

        Jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(
                new EmbedBuilder()
                        .setTitle("Unmuted", null)
                        .setColor(Color.GREEN)
                        .setDescription("You have been unmuted in " + Jarvis.getJda().getGuildById(guildId).getName())
                        .build())
                .queue()));
    }

    public void setupAll() {
        for (Guild guild : Jarvis.getJda().getGuilds()) {
            setup(guild);
        }
    }

    public void setup(Guild guild) {
        try {
            long muteRole = getMuteRole(guild.getIdLong());
            deleteOldRoles(guild, muteRole);
            createMuteRole(guild, muteRole);
        } catch (PermissionException e) {
            e.printStackTrace();
        }
    }

    // Delete roles that have not been added to the database
    private void deleteOldRoles(Guild guild, long muteRole) {
        List<Role> roles = new ArrayList<>();
        roles.addAll(guild.getRolesByName("Jarvis_Mute", true));
        roles.addAll(guild.getRolesByName("new role", true));

        // Do not delete role that is in the db
        if (muteRole != 0) {
            // Role to be deleted exists in db, ignoring
            roles.remove(guild.getRoleById(muteRole));
        }

        for (Role role : roles) {
            try {
                role.delete().reason("Jarvis Mute Role - Deleting old Role").queue();
            } catch (HierarchyException e) {
                module.getLogger().error("Cannot delete old mute role {} in guild {}, role is above any Jarvis groups.", role, role.getGuild());
            }
        }
    }

    private long getMuteRole(long guildId) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT role FROM mute_roles WHERE guild = ?;", guildId);
            if (result.first()) {
                return result.getLong("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void createMuteRole(Guild guild, long muteRole) {
        // If role does not exist on the guild any more, remove it from the database
        if (muteRole != 0 && guild.getRoleById(muteRole) == null) {
            Jarvis.getDatabase().execute("DELETE FROM mute_roles WHERE (guild = ? AND role = ?);", guild.getIdLong(), muteRole);
            muteRole = 0;
        }

        // If the role already exists, don't create another one
        if (muteRole != 0) {
            addMuteRoleToChannels(guild, guild.getRoleById(muteRole));
            processMutedMembers(guild, guild.getRoleById(muteRole));
            return;
        }

        if (!guild.getMember(Jarvis.getJda().getSelfUser()).hasPermission(Permission.MANAGE_ROLES)) {
            module.getLogger().error("No permission {} in guild {}, cannot create mute role.", Permission.MANAGE_ROLES.getName(), guild);
            return;
        }

        guild.getController().createRole().setName("Jarvis_Mute").setColor(0x000001).setPermissions(Permission.MESSAGE_READ).reason("Jarvis Mute Role - Creating").queue(role -> {
            Jarvis.getDatabase().execute("INSERT INTO mute_roles (guild, role) VALUES (?, ?);", guild.getIdLong(), role.getIdLong());

            addMuteRoleToChannels(guild, role);
            processMutedMembers(guild, role);
        });
    }

    private void addMuteRoleToChannels(Guild guild, Role role) {
        List<TextChannel> channels = guild.getTextChannels();

        for (TextChannel channel : channels) {
            if (!guild.getMember(Jarvis.getJda().getSelfUser()).hasPermission(channel, Permission.MANAGE_ROLES)) {
                module.getLogger().error("No permission {} in guild {}, channel {}, cannot add role to channel.", Permission.MANAGE_PERMISSIONS.getName(), guild, channel);
                continue;
            }

            if (channel.getPermissionOverride(role) == null) {
                channel.createPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).reason("Jarvis Mute Role - Creating permission override").queue();
            } else if (!channel.getPermissionOverride(role).getDenied().containsAll(Arrays.asList(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION))) {
                channel.getPermissionOverride(role).getManager().deny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).reason("Jarvis Mute Role - Permissions not set correctly, fixing").queue();
            }
        }
    }

    public void processNewMember(long guildId, long userId) {
        long duration = getMuteDuration(guildId, userId);
        if (DateUtils.timestampApplies(duration)) {
            setMuted(guildId, userId, true);
            startUnmuteThread(guildId, userId, duration);
        }
    }

    private void processMutedMembers(Guild guild, Role role) {
        HashMap<Long, Long> mutes = getMutes(guild.getIdLong());

        if (mutes.size() > 0) {
            module.getLogger().info("Processing {} muted members for {}", mutes.size(), guild);
        }

        for (long userId : mutes.keySet()) {
            if (!DateUtils.timestampApplies(mutes.get(userId))) {
                unmute(guild.getIdLong(), userId);
                continue;
            }

            setMuted(guild, guild.getMemberById(userId), role, true);
            startUnmuteThread(guild.getIdLong(), userId, mutes.get(userId));
        }
    }

    private void setMuted(long guildId, long userId, boolean applied) {
        Guild guild = Jarvis.getJda().getGuildById(guildId);

        setMuted(guild, guild.getMemberById(userId), guild.getRoleById(getMuteRole(guildId)), applied);
    }

    private void setMuted(Guild guild, Member member, Role role, boolean applied) {
        if (member == null) {
            return;
        }

        if (applied) {
            guild.getController().addRolesToMember(member, role).queue();
        } else {
            guild.getController().removeRolesFromMember(member, role).queue();
        }
    }

    private void startUnmuteThread(final long guildId, final long userId, final long duration) {
        Task.builder(module)
                .execute(() -> unmute(guildId, userId))
                .delay(System.currentTimeMillis() - duration, TimeUnit.NANOSECONDS)
                .name("Unmute thread")
                .submit();
    }
}
