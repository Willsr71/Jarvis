package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandCommandRemove extends Command {
    private Jarvis jarvis;

    public CommandCommandRemove(Jarvis jarvis) {
        super("commandremove", "commandremove <name>", "Removes a custom command. Guild specific", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        checkUserPermission(message, Permission.MESSAGE_MANAGE);

        if (args.length != 1) {
            sendFailureMessage(message, "Usage: !commandremove <command>");
            return;
        }

        if (jarvis.commandManager.getCustomCommandResponse(message.getGuild().getIdLong(), args[0]) == null) {
            sendFailureMessage(message, "Command does not exist");
            return;
        }

        sendSuccessEmote(message);
        jarvis.commandManager.removeCustomCommand(message.getGuild().getIdLong(), args[0]);
    }
}
