package sr.will.jarvis.modules.verify;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.verify.command.CommandVerifySettings;
import sr.will.jarvis.modules.verify.event.EventHandlerVerify;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ModuleVerify extends Module {
    private HashMap<Long, GuildVerificationData> guildVerificationData = new HashMap<>();

    public void initialize() {
        setNeededPermissions(
                Permission.MANAGE_ROLES,
                Permission.NICKNAME_MANAGE,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_MANAGE,
                Permission.MESSAGE_ADD_REACTION
        );
        setGuildWhitelist(
                290558097246650369L,
                305772966044631040L
        );
        setDefaultEnabled(false);

        registerEventHandler(new EventHandlerVerify(this));

        registerCommand("verifysettings", new CommandVerifySettings(this));
    }

    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS verify_data(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "channel bigint(20) NOT NULL," +
                "role bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");

        ResultSet result = Jarvis.getDatabase().executeQuery("SELECT guild, channel, role FROM verify_data;");
        try {
            while (result.next()) {
                guildVerificationData.put(result.getLong("guild"), new GuildVerificationData(result.getLong("channel"), result.getLong("role")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stop() {

    }

    public void reload() {

    }

    public void setVerificationData(long guildId, long channelId, long roleId) {
        guildVerificationData.put(guildId, new GuildVerificationData(channelId, roleId));
        Jarvis.getDatabase().execute("INSERT INTO verify_data (guild, channel, role) VALUES (?, ?, ?);", guildId, channelId, roleId);
    }

    public boolean isDataSet(long guildId) {
        return guildVerificationData.containsKey(guildId);
    }

    public GuildVerificationData getVerificationData(long guildId) {
        return guildVerificationData.get(guildId);
    }

    public Long getVerificationChannel(long guildId) {
        return guildVerificationData.get(guildId).channelId;
    }
}
