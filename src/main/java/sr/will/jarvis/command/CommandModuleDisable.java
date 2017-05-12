package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

public class CommandModuleDisable extends Command {
    private Jarvis jarvis;

    public CommandModuleDisable(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        checkUserPermission(message, Permission.ADMINISTRATOR);

        if (args.length != 1) {
            sendFailureMessage(message, "Usage: !moduledisable <module>");
            return;
        }

        Module module = jarvis.moduleManager.getModule(args[0].toLowerCase());

        if (module == null) {
            sendFailureMessage(message, "Invalid module");
            return;
        }

        sendSuccessEmote(message);
        jarvis.moduleManager.disableModule(message.getGuild().getIdLong(), module.getName());
    }

    @Override
    public String getUsage() {
        return "moduledisable <module>";
    }

    @Override
    public String getDescription() {
        return "Disables the specified module";
    }

    @Override
    public boolean getModuleEnabled(long guildId) {
        return true;
    }
}
