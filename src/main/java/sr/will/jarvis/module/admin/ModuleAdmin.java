package sr.will.jarvis.module.admin;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.admin.command.*;
import sr.will.jarvis.module.admin.event.EventHandlerAdmin;
import sr.will.jarvis.module.admin.manager.BanManager;
import sr.will.jarvis.module.admin.manager.MuteManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ModuleAdmin extends Module {
    public BanManager banManager;
    public MuteManager muteManager;

    public ModuleAdmin() {
        super(
                "Admin",
                "Administrative commands such as ban, mute, and clear",
                new ArrayList<>(Arrays.asList(
                        Permission.MANAGE_ROLES,
                        Permission.MANAGE_CHANNEL,
                        Permission.KICK_MEMBERS,
                        Permission.BAN_MEMBERS,
                        Permission.MESSAGE_READ,
                        Permission.MESSAGE_WRITE,
                        Permission.MESSAGE_MANAGE,
                        Permission.MESSAGE_ADD_REACTION
                )),
                true
        );

        banManager = new BanManager(this);
        muteManager = new MuteManager(this);

        registerEventHandler(new EventHandlerAdmin(this));

        registerCommand("ban", new CommandBan(this));
        registerCommand("banlist", new CommandBanList(this));
        registerCommand("bantime", new CommandBanTime(this));
        registerCommand("clear", new CommandClear(this));
        registerCommand("mute", new CommandMute(this));
        registerCommand("mutelist", new CommandMuteList(this));
        registerCommand("mutetime", new CommandMuteTime(this));
        registerCommand("unban", new CommandUnban(this));
        registerCommand("unmute", new CommandUnmute(this));
    }

    @Override
    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS mutes(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "invoker bigint(20)," +
                "duration bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS bans(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "user bigint(20) NOT NULL," +
                "invoker bigint(20)," +
                "duration bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");

        muteManager.setupAll();
        banManager.setup();
    }

    @Override
    public void stop() {
        banManager.stop();
        muteManager.stop();
    }

    @Override
    public void reload() {

    }
}
