package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

public class CommandBotAdd extends Command {
    private Jarvis jarvis;

    public CommandBotAdd(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            CommandUtils.sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        if (jarvis.chatterBotManager.isBotChannel(message.getChannel().getId())) {
            CommandUtils.sendFailureMessage(message, "Bot already active in this channel");
            return;
        }

        CommandUtils.sendSuccessEmote(message);
        jarvis.chatterBotManager.addBot(message.getChannel().getId());
    }
}
