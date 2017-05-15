package sr.will.jarvis.module.chatbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.chatbot.ModuleChatBot;

public class CommandBotRemove extends Command {
    private ModuleChatBot module;

    public CommandBotRemove(ModuleChatBot module) {
        super("botremove", "botremove", "Removes a chat bot from the current channel", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.MESSAGE_MANAGE);

        if (!module.isBotChannel(message.getChannel().getIdLong())) {
            sendFailureMessage(message, "There is no bot active in this channel");
            return;
        }

        sendSuccessEmote(message);
        module.removeBot(message.getChannel().getIdLong());
    }
}
