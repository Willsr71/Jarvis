package sr.will.jarvis.listener;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;

public class ReadyListener extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        Jarvis.getInstance().finishStartup();
    }
}
