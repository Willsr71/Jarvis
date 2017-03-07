package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.awt.*;

public class CommandCommandRemove extends Command {
    private Jarvis jarvis;

    public CommandCommandRemove(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("You don't have permission for that").build()).queue();
            return;
        }

        if (args.length != 1) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("Usage: !commandremove <command>").build()).queue();
            return;
        }

        if (jarvis.commandManager.getCustomCommandResponse(message.getGuild().getId(), args[0]) == null) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("Command does not exist").build()).queue();
            return;
        }

        jarvis.commandManager.removeCustomCommand(message.getGuild().getId(), args[0]);
        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success").setColor(Color.GREEN).setDescription("Command !" + args[0] + " has been removed").build()).queue();
    }
}
