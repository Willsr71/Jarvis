package sr.will.jarvis.module.chatbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.chatbot.ModuleChatBot;

public class CommandBotAdd extends Command {
    private ModuleChatBot module;

    public CommandBotAdd(ModuleChatBot module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.MESSAGE_MANAGE);

        if (module.isBotChannel(message.getChannel().getId())) {
            sendFailureMessage(message, "Bot already active in this channel");
            return;
        }

        sendSuccessEmote(message);
        module.addBot(message.getChannel().getId());
    }
}
