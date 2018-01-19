package sr.will.jarvis.module.flair.event;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.event.EventHandler;
import sr.will.jarvis.event.EventPriority;
import sr.will.jarvis.module.flair.ModuleFlair;

public class EventHandlerSmashBot extends EventHandler {
    private ModuleFlair module;

    public EventHandlerSmashBot(ModuleFlair module) {
        super(module, EventPriority.MEDIUM);
        this.module = module;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReactionAddEvent) {
            onMessageReactionAdd((MessageReactionAddEvent) event);
        } else if (event instanceof MessageReactionRemoveEvent) {
            onMessageReactionRemove((MessageReactionRemoveEvent) event);
        } else if (event instanceof GuildMemberJoinEvent) {
            onGuildMemberJoin((GuildMemberJoinEvent) event);
        }
    }

    private void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!module.isEnabled(event.getGuild().getIdLong())) {
            return;
        }

        if (event.getChannel().getType() != ChannelType.TEXT || event.getUser().isBot()) {
            return;
        }

        event.getChannel().getMessageById(event.getMessageId()).queue(message -> {
            if (Command.getMaxApplicableReactionCount(message.getReactions(), Jarvis.getInstance().config.discord.pinEmotes) >= 5) {
                Command.pinMessage(message);
            }
        });
    }

    private void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (!module.isEnabled(event.getGuild().getIdLong())) {
            return;
        }

        if (event.getChannel().getType() != ChannelType.TEXT || event.getUser().isBot()) {
            return;
        }

        event.getChannel().getMessageById(event.getMessageId()).queue(message -> {
            if (Command.getMaxApplicableReactionCount(message.getReactions(), Jarvis.getInstance().config.discord.pinEmotes) <= 3) {
                Command.unpinMessage(message);
            }
        });
    }

    private void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!module.isEnabled(event.getGuild().getIdLong())) {
            return;
        }

        ModuleFlair.Flair flair = module.getMemberFlair(event.getMember());

        if (flair.roleId != 0) {
            Guild guild = event.getGuild();
            Role role = guild.getRoleById(flair.roleId);
            Member member = event.getMember();
            if (role == null) {
                module.createMemberFlair(member, flair.name, flair.color);
                return;
            }

            guild.getController().addSingleRoleToMember(member, role).queue();
        }
    }
}
