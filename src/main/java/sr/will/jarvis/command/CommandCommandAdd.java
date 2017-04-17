package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandCommandAdd extends Command {
    private Jarvis jarvis;

    public CommandCommandAdd(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        checkUserPermission(message, Permission.MESSAGE_MANAGE);

        if (args.length < 2) {
            sendFailureMessage(message, "Usage: !commandadd <command> <response>");
            return;
        }

        if (args[0].length() > 255) {
            sendFailureMessage(message, "Command \"" + args[0] + "\" is too long");
            return;
        }

        if (jarvis.commandManager.getCustomCommandResponse(message.getGuild().getIdLong(), args[0]) != null) {
            sendFailureMessage(message, "Command already exists");
            return;
        }

        String response = "";
        for (int x = 1; x < args.length; x += 1) {
            response += args[x] + " ";
        }
        response = response.trim();

        sendSuccessEmote(message);
        jarvis.commandManager.addCustomCommand(message.getGuild().getIdLong(), args[0], response);
    }
}
