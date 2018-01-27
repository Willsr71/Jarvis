package sr.will.jarvis.modules.chatbot.event;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sr.will.jarvis.event.EventHandler;
import sr.will.jarvis.event.EventPriority;
import sr.will.jarvis.modules.chatbot.ModuleChatBot;

public class EventHandlerChatBot extends EventHandler {
    private ModuleChatBot module;

    public EventHandlerChatBot(ModuleChatBot module) {
        super(module, EventPriority.LOWEST);
        this.module = module;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            onMessageReceived((MessageReceivedEvent) event);
        }
    }

    private void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (event.getMessage().getContentDisplay().startsWith("!")) {
            return;
        }

        if (module.isBotChannel(event.getChannel().getIdLong())) {
            if (event.getMessage().getContentDisplay().startsWith("<")) {
                return;
            }

            module.sendResponse(event.getMessage());
        }
    }
}
