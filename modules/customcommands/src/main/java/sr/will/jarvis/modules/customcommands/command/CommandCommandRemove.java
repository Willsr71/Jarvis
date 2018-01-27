package sr.will.jarvis.modules.customcommands.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.customcommands.ModuleCustomCommands;

public class CommandCommandRemove extends Command {
    private ModuleCustomCommands module;

    public CommandCommandRemove(ModuleCustomCommands module) {
        super("commandremove", "commandremove <name>", "Removes a custom command. Guild specific", null);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.MESSAGE_MANAGE);

        if (args.length != 1) {
            sendFailureMessage(message, "Usage: !commandremove <command>");
            return;
        }

        if (module.getCustomCommandResponse(message.getGuild().getIdLong(), args[0]) == null) {
            sendFailureMessage(message, "Command does not exist");
            return;
        }

        sendSuccessEmote(message);
        module.removeCustomCommand(message.getGuild().getIdLong(), args[0]);
    }
}
