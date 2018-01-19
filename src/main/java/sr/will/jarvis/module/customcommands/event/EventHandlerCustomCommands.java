package sr.will.jarvis.module.customcommands.event;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sr.will.jarvis.event.EventHandler;
import sr.will.jarvis.event.EventPriority;
import sr.will.jarvis.module.customcommands.ModuleCustomCommands;

public class EventHandlerCustomCommands extends EventHandler {
    private ModuleCustomCommands module;

    public EventHandlerCustomCommands(ModuleCustomCommands module) {
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
        if (event.getMessage().getContentRaw().startsWith("!")) {
            module.processCustomCommand(event.getMessage());
        }
    }
}
