package sr.will.jarvis.module.ohno.event;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sr.will.jarvis.event.EventHandler;
import sr.will.jarvis.event.EventPriority;
import sr.will.jarvis.module.ohno.ModuleOhNo;

public class EventHandlerOhNo extends EventHandler {
    private ModuleOhNo module;

    public EventHandlerOhNo(ModuleOhNo module) {
        super(module, EventPriority.MEDIUM);
        this.module = module;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            onMessageReceived((MessageReceivedEvent) event);
        }
    }

    private void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().toLowerCase().equals("oh no")) {
            return;
        }

        if (!module.isEnabled(event.getGuild().getIdLong())) {
            return;
        }

        module.memeMessage(event.getMessage());
    }
}
