package sr.will.jarvis.modules.info;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.info.command.CommandGuild;
import sr.will.jarvis.modules.info.command.CommandInfo;
import sr.will.jarvis.modules.info.command.CommandRoles;

import java.time.format.DateTimeFormatter;

public class ModuleInfo extends Module {
    public DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION
        );
        setDefaultEnabled(true);

        registerCommand("guild", new CommandGuild(this));
        registerCommand("info", new CommandInfo(this));
        registerCommand("roles", new CommandRoles(this));
    }

    public void finishStart() {

    }

    public void stop() {

    }

    public void reload() {

    }
}
