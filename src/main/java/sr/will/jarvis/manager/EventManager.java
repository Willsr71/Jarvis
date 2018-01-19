package sr.will.jarvis.manager;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
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
        System.out.println("======================");
        for (EventPriority priority : EventPriority.values()) {
            System.out.println("PRIORITY: " + priority.name());
            for (EventHandler eventHandler : getEventHandlersByPriority(priority)) {
                String name = "Jarvis";
                if (eventHandler.getModule() != null) {
                    name = eventHandler.getModule().getName();
                }
                System.out.println("HANDLER MODULE: " + name);

                eventHandler.onEvent(event);
            }
        }
        System.out.println("======================");
    }

    @Override
    public void onEvent(Event event) {
        Thread thread = new Thread(() -> processEvent(event));

        /*
        long startTime = new Date().getTime();
        System.out.println("Thread " + thread.getId() + " started (event)");
        */

        thread.start();

        /*
        long time = new Date().getTime() - startTime;
        System.out.println("Thread " + thread.getId() + " finished (event) (" + time + "ms)");
        */
    }
}
