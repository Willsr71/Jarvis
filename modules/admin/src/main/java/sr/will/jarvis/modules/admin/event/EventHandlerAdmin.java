package sr.will.jarvis.modules.admin.event;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sr.will.jarvis.event.EventHandler;
import sr.will.jarvis.event.EventPriority;
import sr.will.jarvis.modules.admin.ModuleAdmin;

public class EventHandlerAdmin extends EventHandler {
    private ModuleAdmin module;

    public EventHandlerAdmin(ModuleAdmin module) {
        super(module, EventPriority.HIGHEST);
        this.module = module;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            onMessageReceived((MessageReceivedEvent) event);
        } else if (event instanceof GuildMemberJoinEvent) {
            onGuildMemberJoin((GuildMemberJoinEvent) event);
        } else if (event instanceof GuildJoinEvent) {
            onGuildJoin((GuildJoinEvent) event);
        }
    }

    private void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) {
            return;
        }

        if (module.muteManager.isMuted(event.getGuild().getIdLong(), event.getAuthor().getIdLong())) {
            event.getMessage().delete().queue();
            module.getLogger().info("Member is muted but still sent a message, deleting");

            if (event.getGuild().getMember(event.getAuthor()).hasPermission(Permission.ADMINISTRATOR)) {
                return;
            }

            module.muteManager.setup(event.getGuild());
        }
    }

    private void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!module.isEnabled(event.getGuild().getIdLong())) {
            return;
        }

        module.muteManager.processNewMember(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong());
    }

    private void onGuildJoin(GuildJoinEvent event) {
        module.muteManager.setup(event.getGuild());
    }
}
