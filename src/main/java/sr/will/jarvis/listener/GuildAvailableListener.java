package sr.will.jarvis.listener;

import net.dv8tion.jda.core.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.admin.ModuleAdmin;

public class GuildAvailableListener extends ListenerAdapter {
    private Jarvis jarvis;
    private ModuleAdmin moduleAdmin;

    public GuildAvailableListener(Jarvis jarvis) {
        this.jarvis = jarvis;
        this.moduleAdmin = (ModuleAdmin) jarvis.moduleManager.getModule("admin");
    }

    @Override
    public void onGuildAvailable(GuildAvailableEvent event) {
        moduleAdmin.muteManager.setup(event.getGuild());
    }
}
