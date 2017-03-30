package sr.will.jarvis.listener;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.admin.ModuleAdmin;

public class MessageListener extends ListenerAdapter {
    private Jarvis jarvis;
    private ModuleAdmin moduleAdmin;

    public MessageListener(Jarvis jarvis) {
        this.jarvis = jarvis;
        this.moduleAdmin = (ModuleAdmin) jarvis.moduleManager.getModule("admin");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT) {
            return;
        }

        jarvis.messagesReceived += 1;

        if (event.getAuthor().isBot()) {
            return;
        }

        if (moduleAdmin.muteManager.isMuted(event.getGuild().getId(), event.getAuthor().getId())) {
            event.getMessage().delete().queue();
            System.out.println("deleting message");

            if (event.getGuild().getMember(event.getAuthor()).hasPermission(Permission.ADMINISTRATOR)) {
                return;
            }

            moduleAdmin.muteManager.setup(event.getGuild());
            return;
        }

        if (event.getMessage().getContent().startsWith("!")) {
            jarvis.commandManager.executeCommand(event.getMessage());
            return;
        }

        if (jarvis.chatterBotManager.isBotChannel(event.getChannel().getId())) {
            if (event.getMessage().getContent().startsWith("<")) {
                return;
            }

            jarvis.chatterBotManager.sendResponse(event.getMessage());
            return;
        }

        jarvis.levelManager.increase(event.getGuild().getId(), event.getAuthor().getId(), event.getChannel());
    }
}
