package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandBotRemove extends Command {
    private Jarvis jarvis;

    public CommandBotRemove(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            message.getChannel().sendMessage("`You don't have permission for that`").queue();
            return;
        }

        if (!jarvis.chatterBotManager.isBotChannel(message.getChannel().getId())) {
            message.getChannel().sendMessage("`There is no bot active in this channel`").queue();
            return;
        }

        message.getChannel().sendMessage("`Bot removed from channel`").queue();
        jarvis.chatterBotManager.removeBot(message.getChannel().getId());
    }
}
