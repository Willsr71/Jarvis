package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.awt.*;

public class CommandBotRemove extends Command {
    private Jarvis jarvis;

    public CommandBotRemove(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("You don't have permission for that").build()).queue();
            return;
        }

        if (!jarvis.chatterBotManager.isBotChannel(message.getChannel().getId())) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("There is no bot active in this channel").build()).queue();
            return;
        }

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success").setColor(Color.GREEN).setDescription("Bot removed from channel").build()).queue();
        jarvis.chatterBotManager.removeBot(message.getChannel().getId());
    }
}
