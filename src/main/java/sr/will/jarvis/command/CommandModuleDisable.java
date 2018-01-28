package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

public class CommandModuleDisable extends Command {
    private Jarvis jarvis;

    public CommandModuleDisable(Jarvis jarvis) {
        super("moduledisable", "moduledisable <module>", "Disables the specified module", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        checkUserPermission(message, Permission.ADMINISTRATOR);

        if (args.length != 1) {
            sendFailureMessage(message, "Usage: !moduledisable <module>");
            return;
        }

        Module module = jarvis.moduleManager.getModule(args[0]);

        if (module == null) {
            sendFailureMessage(message, "Invalid module");
            return;
        }

        if (!module.isEnabled(message.getGuild().getIdLong())) {
            sendFailureMessage(message, "Module is already disabled");
            return;
        }

        sendSuccessEmote(message);
        jarvis.moduleManager.disableModule(message.getGuild().getIdLong(), module.getDescription().getName());
    }
}
