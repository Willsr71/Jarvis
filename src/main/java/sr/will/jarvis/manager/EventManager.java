package sr.will.jarvis.manager;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import net.noxal.common.Task;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.event.EventHandler;
import sr.will.jarvis.event.EventPriority;
import sr.will.jarvis.module.Module;

import java.util.ArrayList;


public class EventManager implements EventListener {
    private Jarvis jarvis;

    private ArrayList<EventHandler> eventHandlers = new ArrayList<>();

    public EventManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void registerHandler(EventHandler handler) {
        eventHandlers.add(handler);
    }

    public void unregisterHandler(EventHandler handler) {
        eventHandlers.remove(handler);
    }

    public void unregisterHandlers(Module module) {
        getEventHandlersByModule(module).forEach(this::unregisterHandler);
    }

    public ArrayList<EventHandler> getEventHandlers() {
        return eventHandlers;
    }

    public ArrayList<EventHandler> getEventHandlersByModule(Module module) {
        ArrayList<EventHandler> handlers = new ArrayList<>();

        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.getModule() == module) {
                handlers.add(eventHandler);
            }
        }

        return handlers;
    }

    public ArrayList<EventHandler> getEventHandlersByPriority(EventPriority priority) {
        ArrayList<EventHandler> handlers = new ArrayList<>();

        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.getPriority() == priority) {
                handlers.add(eventHandler);
            }
        }

        return handlers;
    }

    private void processEvent(Event event) {
        for (EventPriority priority : EventPriority.values()) {
            for (EventHandler eventHandler : getEventHandlersByPriority(priority)) {
                eventHandler.onEvent(event);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        Task.builder(jarvis)
                .execute(() -> processEvent(event))
                .name("Event-" + event.getClass().getSimpleName())
                .submit();
    }
}
