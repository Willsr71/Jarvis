package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.awt.*;

public class CommandCommandAdd extends Command {
    private Jarvis jarvis;

    public CommandCommandAdd(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("You don't have permission for that").build()).queue();
            return;
        }

        if (args.length < 2) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("Usage: !commandadd <command> <response>").build()).queue();
            return;
        }

        if (args[0].length() > 255) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("Command \"" + args[0] + "\" is too long").build()).queue();
            return;
        }

        if (jarvis.commandManager.getCustomCommandResponse(message.getGuild().getId(), args[0]) != null) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("Command already exists").build()).queue();
            return;
        }

        String response = "";
        for (int x = 1; x < args.length; x += 1) {
            response += args[x] + " ";
        }
        response = response.trim();

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success", "https://jarvis.will.sr").setColor(Color.GREEN).setDescription("Command !" + args[0] + " has been added").build()).queue();
        jarvis.commandManager.addCustomCommand(message.getGuild().getId(), args[0], response);
    }
}
