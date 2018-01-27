package sr.will.jarvis.modules.ohno.event;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sr.will.jarvis.event.EventHandler;
import sr.will.jarvis.event.EventPriority;
import sr.will.jarvis.modules.ohno.ModuleOhNo;

import java.util.ArrayList;
import java.util.Arrays;

public class EventHandlerOhNo extends EventHandler {
    private ModuleOhNo module;

    private ArrayList<String> respondStrings = new ArrayList<>(Arrays.asList("oh no", "oh dear"));

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
        if (!respondStrings.contains(event.getMessage().getContentDisplay().toLowerCase())) {
            return;
        }

        if (!module.isEnabled(event.getGuild().getIdLong())) {
            return;
        }

        module.memeMessage(event.getMessage());
    }
}
