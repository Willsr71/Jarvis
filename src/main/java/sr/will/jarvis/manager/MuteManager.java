package sr.will.jarvis.manager;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class MuteManager {
    private Jarvis jarvis;

    public MuteManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public HashMap<String, Long> getMutes(String guildId) {
        HashMap<String, Long> mutes = new HashMap<>();
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT user, duration FROM mutes WHERE (guild = ?);", guildId);
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
                setMuteRole(guild);
            } catch (PermissionException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMuteRole(Guild guild) {
        // Delete old role(s)
        List<Role> roles = guild.getRolesByName("Jarvis_Mute", true);
        roles.addAll(guild.getRolesByName("new role", true));
        for (Role role : roles) {
            role.delete().queue();
        }

        guild.getController().createRole().queue((role) -> {
            role.getManager().setName("Jarvis_Mute").block();
            role.getManager().setColor(Color.black).block();
            role.getManager().setMentionable(false).queue();
            role.getManager().setPermissions(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue();
        });
    }

    public void applyMute(String userId, String guildId) {
        Guild guild = jarvis.getJda().getGuildById(guildId);
        Member member = guild.getMemberById(userId);
        List<TextChannel> channels = guild.getTextChannels();
    }
}
