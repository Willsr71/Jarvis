package sr.will.jarvis.listener;

import net.dv8tion.jda.core.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;

public class GuildAvailableListener extends ListenerAdapter {
    private Jarvis jarvis;

    public GuildAvailableListener(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void onGuildAvailable(GuildAvailableEvent event) {
        jarvis.muteManager.setup(event.getGuild());
    }
}
