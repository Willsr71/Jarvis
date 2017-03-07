package sr.will.jarvis.manager;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.noxal.common.util.DateUtils;
import net.noxal.common.util.Logger;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class MuteManager {
    private Jarvis jarvis;

    public MuteManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public HashMap<String, Long> getMutes(String guildId) {
        HashMap<String, Long> mutes = new HashMap<>();
        long time = Math.floorDiv(System.currentTimeMillis(), 1000);
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT user, duration FROM mutes WHERE (guild = ? AND (duration = -1 OR duration >= ?) AND id = (SELECT max(id) FROM mutes));", guildId, time);
            while (result.next()) {
                mutes.put(result.getString("user"), result.getLong("duration"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mutes;
    }

    public long getMuteDuration(String guildId, String userId) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT duration FROM mutes WHERE (guild = ? AND user = ?) ORDER BY id DESC LIMIT 1;", guildId, userId);
            if (result.first()) {
                return result.getLong("duration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isMuted(String userId, String guildId) {
        return DateUtils.timestampApplies(getMuteDuration(guildId, userId));
    }

    public void mute(String userId, String invokerId, String guildId) {
        mute(userId, invokerId, guildId, -1);
    }

    public void mute(String userId, String invokerId, String guildId, long duration) {
        jarvis.database.execute("INSERT INTO mutes (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, invokerId, duration);
    }

    public void unmute(String userId, String guildId) {
        jarvis.database.execute("INSERT INTO mutes (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, null, 0);
    }

    public void setMuteRoles() {
        for (Guild guild : jarvis.getJda().getGuilds()) {
            try {
                deleteOldRoles(guild);
                createMuteRole(guild);
            } catch (PermissionException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteOldRoles(Guild guild) {
        List<Role> roles = new ArrayList<>();
        roles.addAll(guild.getRolesByName("Jarvis_Mute", true));
        roles.addAll(guild.getRolesByName("new role", true));
        for (Role role : roles) {
            role.delete().queue(aVoid -> {
                System.out.println("Deleted role " + role.getName() + " from guild " + role.getGuild().getName());
            });
        }
    }

    public void createMuteRole(Guild guild) {
        guild.getController().createRole().queue((role) -> {
            role.getManager().setName("Jarvis_Mute").queue(aVoid -> {
                role.getManager().setPermissions().queue(aVoid1 -> {
                    role.getManager().setColor(Color.black).queue(aVoid2 -> {
                        role.getManager().setMentionable(false).queue(aVoid3 -> {
                            addMuteRoleToChannels(guild, role);
                            System.out.println("Created mute role in guild " + guild.getName());
                        });
                    });
                });
            });
        });
    }

    public void addMuteRoleToChannels(Guild guild, Role role) {
        List<TextChannel> channels = guild.getTextChannels();

        for (TextChannel channel : channels) {
            channel.createPermissionOverride(role).queue(aVoid -> {
                channel.getPermissionOverride(role).getManager().deny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue();
            });
        }
    }

    public void applyMute(String userId, String guildId) {
        Guild guild = jarvis.getJda().getGuildById(guildId);
        Member member = guild.getMemberById(userId);

        guild.getController().addRolesToMember(member, guild.getRolesByName("Jarvis_Mute", true).get(0));
    }
}
