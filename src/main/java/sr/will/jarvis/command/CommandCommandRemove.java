package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandCommandRemove extends Command {
    private Jarvis jarvis;

    public CommandCommandRemove(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            message.getChannel().sendMessage("You don't have permission for that.").queue();
            return;
        }

        if (args.length != 1) {
            message.getChannel().sendMessage("Usage: !removecommand <command>").queue();
            return;
        }

        if (jarvis.commandManager.getCustomCommandResponse(message.getGuild().getId(), args[0]) == null) {
            message.getChannel().sendMessage("Command does not exist").queue();
            return;
        }

        jarvis.commandManager.removeCustomCommand(message.getGuild().getId(), args[0]);
        message.getChannel().sendMessage("Command `" + args[0] + "` has been removed").queue();
    }
}
