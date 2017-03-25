package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

public class CommandCommandAdd extends Command {
    private Jarvis jarvis;

    public CommandCommandAdd(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            CommandUtils.sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        if (args.length < 2) {
            CommandUtils.sendFailureMessage(message, "Usage: !commandadd <command> <response>");
            return;
        }

        if (args[0].length() > 255) {
            CommandUtils.sendFailureMessage(message, "Command \"" + args[0] + "\" is too long");
            return;
        }

        if (jarvis.commandManager.getCustomCommandResponse(message.getGuild().getId(), args[0]) != null) {
            CommandUtils.sendFailureMessage(message, "Command already exists");
            return;
        }

        String response = "";
        for (int x = 1; x < args.length; x += 1) {
            response += args[x] + " ";
        }
        response = response.trim();

        CommandUtils.sendSuccessEmote(message);
        jarvis.commandManager.addCustomCommand(message.getGuild().getId(), args[0], response);
    }
}
