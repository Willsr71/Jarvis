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
        checkUserPermission(message, Permission.MESSAGE_MANAGE);

        if (jarvis.chatterBotManager.isBotChannel(message.getChannel().getId())) {
            sendFailureMessage(message, "Bot already active in this channel");
            return;
        }

        sendSuccessEmote(message);
        jarvis.chatterBotManager.addBot(message.getChannel().getId());
    }
}
