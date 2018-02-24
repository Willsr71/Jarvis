package sr.will.jarvis.modules.levels.event;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sr.will.jarvis.event.EventHandler;
import sr.will.jarvis.event.EventPriority;
import sr.will.jarvis.modules.levels.ModuleLevels;

public class EventHandlerLevels extends EventHandler {
    private ModuleLevels module;

    public EventHandlerLevels(ModuleLevels module) {
        super(module, EventPriority.MEDIUM);
        this.module = module;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            onMessageReceived((MessageReceivedEvent) event);
        }
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getChannelType() != ChannelType.TEXT) {
            return;
        }

        module.increase(event.getMessage());
    }
}
