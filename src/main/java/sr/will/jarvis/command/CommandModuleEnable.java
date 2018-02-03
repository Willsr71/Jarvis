package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

public class CommandModuleEnable extends Command {
    private Jarvis jarvis;

    public CommandModuleEnable(Jarvis jarvis) {
        super("moduleenable", "moduleenable <module>", "Enables the specified module", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        checkUserPermission(message, Permission.ADMINISTRATOR);

        if (args.length != 1) {
            sendFailureMessage(message, "Usage: !moduleenable <module>");
            return;
        }

        Module module = jarvis.moduleManager.getModule(args[0]);

        if (module == null) {
            sendFailureMessage(message, "Invalid module");
            return;
        }

        if (!module.isGuildWhitelisted(message.getGuild().getIdLong())) {
            sendFailureMessage(message, "Module is not allowed on this guild");
            return;
        }

        if (module.isEnabled(message.getGuild().getIdLong())) {
            sendFailureMessage(message, "Module is already enabled");
            return;
        }

        sendSuccessEmote(message);
        jarvis.moduleManager.enableModule(message.getGuild().getIdLong(), module.getDescription().getName());
    }
}
