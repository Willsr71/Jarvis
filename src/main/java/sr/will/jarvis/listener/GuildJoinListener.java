package sr.will.jarvis.listener;

import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.admin.ModuleAdmin;

public class GuildJoinListener extends ListenerAdapter {
    private Jarvis jarvis;
    private ModuleAdmin moduleAdmin;

    public GuildJoinListener(Jarvis jarvis) {
        this.jarvis = jarvis;

        moduleAdmin = (ModuleAdmin) jarvis.moduleManager.getModule("admin");
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        System.out.println(String.format("Joined guild %s (%s)", event.getGuild().getName(), event.getGuild().getId()));

        jarvis.moduleManager.enableDefaultModules(event.getGuild().getIdLong());
        moduleAdmin.muteManager.setup(event.getGuild());
    }
}
