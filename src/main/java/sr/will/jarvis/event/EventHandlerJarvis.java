package sr.will.jarvis.event;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.manager.Stats;

public class EventHandlerJarvis extends EventHandler {
    private Jarvis jarvis;

    public EventHandlerJarvis(Jarvis jarvis) {
        super(null, EventPriority.HIGH);
        this.jarvis = jarvis;
    }

    @Override
    public void onEvent(Event event) {
        Stats.processEvent(event);

        if (event instanceof MessageReceivedEvent) {
            onMessageReceived((MessageReceivedEvent) event);
        } else if (event instanceof ReadyEvent) {
            onReady((ReadyEvent) event);
        } else if (event instanceof GuildJoinEvent) {
            onGuildJoin((GuildJoinEvent) event);
        } else if (event instanceof GuildLeaveEvent) {
            onGuildLeave((GuildLeaveEvent) event);
        }
    }

    private void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT) {
            return;
        }

        if (event.getAuthor().isBot()) {
            return;
        }

        if (event.getMessage().getContentRaw().startsWith("!")) {
            jarvis.commandManager.executeCommand(event.getMessage());
        }
    }

    private void onReady(ReadyEvent event) {
        jarvis.finishStartup();
    }

    private void onGuildJoin(GuildJoinEvent event) {
        System.out.println(String.format("Joined guild %s (%s)", event.getGuild().getName(), event.getGuild().getId()));

        jarvis.moduleManager.enableDefaultModules(event.getGuild().getIdLong());
    }

    private void onGuildLeave(GuildLeaveEvent event) {
        System.out.println(String.format("Left guild %s (%s)", event.getGuild().getName(), event.getGuild().getId()));

        jarvis.moduleManager.disableAllModules(event.getGuild().getIdLong());
    }
}
