package sr.will.jarvis.modules.flair;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.flair.command.*;
import sr.will.jarvis.modules.flair.event.EventHandlerSmashBot;

import java.awt.*;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ModuleFlair extends Module {

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE
        );
        setGuildWhitelist(
                228571366729842688L,
                290558097246650369L,
                263504633077432321L
        );
        setDefaultEnabled(false);

        registerEventHandler(new EventHandlerSmashBot(this));

        registerCommand("flair", new CommandFlair(this));
        registerCommand("flairgetcolor", new CommandFlairGetColor(this));
        registerCommand("flairimport", new CommandFlairImport(this));
        registerCommand("flairlist", new CommandFlairList(this));
        registerCommand("flairsetcolor", new CommandFlairSetColor(this));
        registerCommand("flairsetname", new CommandFlairSetName(this));
    }

    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS flairs(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "role bigint(20)," +
                "name varchar(255) NOT NULL," +
                "color char(7)," +
                "PRIMARY KEY (id));");
    }

    public void stop() {

    }

    public void reload() {

    }

    public void createMemberFlair(Member member, String name, Color color) {
        GuildController guildController = member.getGuild().getController();

        guildController.createRole()
                .setPermissions(Permission.MESSAGE_READ)
                .setName(name)
                .setColor(color)
                .queue(role -> {
                    guildController.addRolesToMember(member, role).queue();
                    setFlairRole(member, role.getIdLong());
                }, role -> {
                    getLogger().error("Failed to create role");
                });
    }

    public void setFlairRole(Member member, long roleId) {
        Jarvis.getDatabase().execute("UPDATE flairs SET role = ? WHERE (guild = ? AND user = ?);", roleId, member.getGuild().getIdLong(), member.getUser().getIdLong());
    }

    public void setFlairName(Member member, String name) {
        Jarvis.getDatabase().execute("UPDATE flairs SET name = ? WHERE (guild = ? AND user = ?);", name, member.getGuild().getIdLong(), member.getUser().getIdLong());

        Flair flair = getMemberFlair(member);
        Role role = member.getGuild().getRoleById(flair.roleId);

        if (role == null) {
            createMemberFlair(member, name, flair.color);
            return;
        }

        role.getManager().setName(name).queue();
    }

    public void setFlairColor(Member member, Color color) {
        Jarvis.getDatabase().execute("UPDATE flairs SET color = ? WHERE (guild = ? AND user = ?);", getHexFromColor(color), member.getGuild().getIdLong(), member.getUser().getIdLong());

        Flair flair = getMemberFlair(member);
        Role role = member.getGuild().getRoleById(flair.roleId);

        if (role == null) {
            createMemberFlair(member, flair.name, color);
            return;
        }

        role.getManager().setColor(color).queue();
    }

    public void addMemberFlair(long guildId, long userId, String name, Color color) {
        Jarvis.getDatabase().execute("INSERT INTO flairs (guild, user, role, name, color) VALUES (?, ?, ?, ?, ?);", guildId, userId, 0, name, getHexFromColor(color));
        createMemberFlair(Jarvis.getJda().getGuildById(guildId).getMemberById(userId), name, color);
    }

    public Flair getMemberFlair(Member member) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT role, name, color FROM flairs WHERE (guild = ? AND user = ?);", member.getGuild().getIdLong(), member.getUser().getIdLong());

            if (!result.first()) {
                addMemberFlair(member.getGuild().getIdLong(), member.getUser().getIdLong(), member.getUser().getName(), Color.WHITE);
                return new Flair(member.getGuild().getIdLong(), member.getUser().getIdLong(), 0, member.getUser().getName(), Color.WHITE);
            }

            return new Flair(member.getGuild().getIdLong(), member.getUser().getIdLong(), result.getLong("role"), result.getString("name"), getColorFromHex(result.getString("color")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashMap<Long, String> getMemberFlairs(long guildId) {
        HashMap<Long, String> memberFlairs = new HashMap<>();
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT user, name FROM flairs WHERE (guild = ?);", guildId);

            while (result.next()) {
                memberFlairs.put(result.getLong("user"), result.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberFlairs;
    }

    public Color getColorFromHex(String colorString) {
        try {
            return Color.decode(colorString);
        } catch (NumberFormatException ignored) {

        }

        try {
            Field field = Color.class.getField(colorString);
            return (Color) field.get(null);
        } catch (Exception ignored) {

        }

        return null;
    }

    public String getHexFromColor(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    public class Flair {
        public long guildId;
        public long userId;
        public long roleId;
        public String name;
        public Color color;

        public Flair(long guildId, long userId, long roleId, String name, Color color) {
            this.guildId = guildId;
            this.userId = userId;
            this.roleId = roleId;
            this.name = name;
            this.color = color;
        }
    }
}
