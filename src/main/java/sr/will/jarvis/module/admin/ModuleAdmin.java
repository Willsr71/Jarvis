package sr.will.jarvis.module.admin;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.admin.command.*;
import sr.will.jarvis.module.admin.manager.BanManager;
import sr.will.jarvis.module.admin.manager.MuteManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ModuleAdmin extends Module {
    public BanManager banManager;
    public MuteManager muteManager;
    private Jarvis jarvis;

    public ModuleAdmin(Jarvis jarvis) {
        this.jarvis = jarvis;

        banManager = new BanManager(this);
        muteManager = new MuteManager(this);

        jarvis.commandManager.registerCommand("ban", new CommandBan(this));
        jarvis.commandManager.registerCommand("banlist", new CommandBanList(this));
        jarvis.commandManager.registerCommand("bantime", new CommandBanTime(this));
        jarvis.commandManager.registerCommand("mute", new CommandMute(this));
        jarvis.commandManager.registerCommand("mutelist", new CommandMuteList(this));
        jarvis.commandManager.registerCommand("mutetime", new CommandMuteTime(this));
        jarvis.commandManager.registerCommand("unban", new CommandUnban(this));
        jarvis.commandManager.registerCommand("unmute", new CommandUnmute(this));
    }

    @Override
    public void finishStart() {
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

    @Override
    public String getName() {
        return "Admin";
    }

    @Override
    public String getHelpText() {
        return "Administrative commands such as ban and mute";
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return new ArrayList<>(Arrays.asList(
                Permission.MANAGE_ROLES,
                Permission.MANAGE_CHANNEL,
                Permission.KICK_MEMBERS,
                Permission.BAN_MEMBERS,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_MANAGE,
                Permission.MESSAGE_ADD_REACTION
        ));
    }

    @Override
    public boolean isDefaultEnabled() {
        return false;
    }
}
