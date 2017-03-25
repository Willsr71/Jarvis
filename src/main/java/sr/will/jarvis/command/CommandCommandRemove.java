package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

public class CommandCommandRemove extends Command {
    private Jarvis jarvis;

    public CommandCommandRemove(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            CommandUtils.sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        if (args.length != 1) {
            CommandUtils.sendFailureMessage(message, "Usage: !commandremove <command>");
            return;
        }

        if (jarvis.commandManager.getCustomCommandResponse(message.getGuild().getId(), args[0]) == null) {
            CommandUtils.sendFailureMessage(message, "Command does not exist");
            return;
        }

        CommandUtils.sendSuccessEmote(message);
        jarvis.commandManager.removeCustomCommand(message.getGuild().getId(), args[0]);
    }
}
