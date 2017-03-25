package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

public class CommandBotRemove extends Command {
    private Jarvis jarvis;

    public CommandBotRemove(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.MESSAGE_MANAGE)) {
            CommandUtils.sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        if (!jarvis.chatterBotManager.isBotChannel(message.getChannel().getId())) {
            CommandUtils.sendFailureMessage(message, "There is no bot active in this channel");
            return;
        }

        CommandUtils.sendSuccessEmote(message);
        jarvis.chatterBotManager.removeBot(message.getChannel().getId());
    }
}
