package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

public class CommandModuleEnable extends Command {
    private Jarvis jarvis;

    public CommandModuleEnable(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        checkUserPermission(message, Permission.ADMINISTRATOR);

        if (args.length != 1) {
            sendFailureMessage(message, "Usage: !moduleenable <module>");
            return;
        }

        Module module = jarvis.moduleManager.getModule(args[0].toLowerCase());

        if (module == null) {
            sendFailureMessage(message, "Invalid module");
            return;
        }

        sendSuccessEmote(message);
        jarvis.moduleManager.enableModule(message.getGuild().getIdLong(), module.getName());
    }
}
