package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandAddCommand extends Command {
    private Jarvis jarvis;

    public CommandAddCommand(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            message.getChannel().sendMessage("You don't have permission for that.").queue();
            return;
        }

        if (args.length < 2) {
            message.getChannel().sendMessage("Usage: !addcommand <command> <response>").queue();
            return;
        }

        if (args[0].length() > 255) {
            message.getChannel().sendMessage("Command \"" + args[0] + "\" is too long").queue();
            return;
        }

        if (jarvis.commandManager.getCustomCommandResponse(message.getGuild().getId(), args[0]) != null) {
            message.getChannel().sendMessage("Command already exists").queue();
            return;
        }

        String response = "";
        for (int x = 1; x < args.length; x += 1) {
            response += args[x] + " ";
        }
        response = response.trim();

        jarvis.commandManager.addCustomCommand(message.getGuild().getId(), args[0], response);
        message.getChannel().sendMessage("Command `!" + args[0] + "`has been added with response\n" + response).queue();
    }
}
