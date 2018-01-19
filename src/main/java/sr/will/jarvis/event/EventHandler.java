package sr.will.jarvis.event;

import net.dv8tion.jda.core.events.Event;
import sr.will.jarvis.module.Module;

public abstract class EventHandler {
    private Module module;
    private EventPriority priority;

    protected EventHandler(Module module, EventPriority priority) {
        this.module = module;
        this.priority = priority;
    }

    public abstract void onEvent(Event event);

    public Module getModule() {
        return module;
    }

    public EventPriority getPriority() {
        return priority;
    }
}
