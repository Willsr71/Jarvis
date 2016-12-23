package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandBotAdd extends Command {
    private Jarvis jarvis;

    public CommandBotAdd(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            message.getChannel().sendMessage("You don't have permission for that.").queue();
            return;
        }

        if (jarvis.chatterBotManager.isBotChannel(message.getChannel().getId())) {
            message.getChannel().sendMessage("Bot already active in this channel").queue();
            return;
        }

        message.getChannel().sendMessage("Bot added to channel.").queue();
        jarvis.chatterBotManager.addBot(message.getChannel().getId());
    }
}
