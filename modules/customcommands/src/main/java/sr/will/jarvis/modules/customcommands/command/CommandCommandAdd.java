package sr.will.jarvis.modules.customcommands.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.customcommands.ModuleCustomCommands;

public class CommandCommandAdd extends Command {
    private ModuleCustomCommands module;

    public CommandCommandAdd(ModuleCustomCommands module) {
        super("commandadd", "commandadd <name> <content>", "Adds a custom command that responds with the response field. Guild specific", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.MESSAGE_MANAGE);

        if (args.length < 2) {
            sendFailureMessage(message, "Usage: !commandadd <command> <response>");
            return;
        }

        if (args[0].length() > 255) {
            sendFailureMessage(message, "Command \"" + args[0] + "\" is too long");
            return;
        }

        if (module.getCustomCommandResponse(message.getGuild().getIdLong(), args[0]) != null) {
            sendFailureMessage(message, "Command already exists");
            return;
        }

        String response = "";
        for (int x = 1; x < args.length; x += 1) {
            response += args[x] + " ";
        }
        response = response.trim();

        sendSuccessEmote(message);
        module.addCustomCommand(message.getGuild().getIdLong(), args[0], response);
    }
}
