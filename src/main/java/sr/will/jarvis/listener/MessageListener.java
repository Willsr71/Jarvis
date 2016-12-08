package sr.will.jarvis.listener;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;

public class MessageListener extends ListenerAdapter {
    private Jarvis jarvis;

    public MessageListener(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) {
            return;
        }

        if (jarvis.muteManager.isMuted(event.getAuthor().getId(), event.getGuild().getId())) {
            event.getMessage().deleteMessage().queue();
            return;
        }

        if (event.getMessage().getContent().startsWith("!")) {
            jarvis.commandHandler.executeCommand(event.getMessage());
            return;
        }
    }
}
