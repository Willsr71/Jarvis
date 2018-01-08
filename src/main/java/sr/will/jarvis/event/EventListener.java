package sr.will.jarvis.event;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.levels.ModuleLevels;
import sr.will.jarvis.module.smashbot.ModuleSmashBot;

import java.util.Date;

public class EventListener extends ListenerAdapter {
    private Jarvis jarvis;

    private ModuleLevels moduleLevels;
    private ModuleSmashBot moduleSmashBot;

    public EventListener(Jarvis jarvis) {
        this.jarvis = jarvis;
        this.moduleLevels = (ModuleLevels) jarvis.moduleManager.getModule("levels");
        this.moduleSmashBot = (ModuleSmashBot) jarvis.moduleManager.getModule("smashbot");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        startThread(() -> {
            if (event.getChannel().getType() != ChannelType.TEXT) {
                return;
            }

            jarvis.messagesReceived += 1;

            if (event.getAuthor().isBot()) {
                return;
            }

            if (event.getMessage().getContent().startsWith("!")) {
                jarvis.commandManager.executeCommand(event.getMessage());
                return;
            }

            moduleLevels.increase(event.getMessage());
        });
    }

    @Override
    public void onReady(ReadyEvent event) {
        startThread(() -> {
            jarvis.finishStartup();
        });
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        startThread(() -> {
            System.out.println(String.format("Joined guild %s (%s)", event.getGuild().getName(), event.getGuild().getId()));

            jarvis.moduleManager.enableDefaultModules(event.getGuild().getIdLong());
        });
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        startThread(() -> {
            if (!moduleSmashBot.isEnabled(event.getGuild().getIdLong())) {
                return;
            }

            if (event.getChannel().getType() != ChannelType.TEXT || event.getUser().isBot()) {
                return;
            }

            event.getChannel().getMessageById(event.getMessageId()).queue(message -> {
                if (Command.getMaxApplicableReactionCount(message.getReactions(), jarvis.config.discord.pinEmotes) >= 5) {
                    Command.pinMessage(message);
                }
            });
        });
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        startThread(() -> {
            if (!moduleSmashBot.isEnabled(event.getGuild().getIdLong())) {
                return;
            }

            if (event.getChannel().getType() != ChannelType.TEXT || event.getUser().isBot()) {
                return;
            }

            event.getChannel().getMessageById(event.getMessageId()).queue(message -> {
                if (Command.getMaxApplicableReactionCount(message.getReactions(), jarvis.config.discord.pinEmotes) <= 3) {
                    Command.unpinMessage(message);
                }
            });
        });
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        startThread(() -> {
            if (!moduleSmashBot.isEnabled(event.getGuild().getIdLong())) {
                return;
            }

            ModuleSmashBot.Flair flair = moduleSmashBot.getMemberFlair(event.getMember());

            if (flair.roleId != 0) {
                Guild guild = event.getGuild();
                Role role = guild.getRoleById(flair.roleId);
                Member member = event.getMember();
                if (role == null) {
                    moduleSmashBot.createMemberFlair(member, flair.name, flair.color);
                    return;
                }

                guild.getController().addSingleRoleToMember(member, role).queue();
            }
        });
    }

    private void startThread(Runnable runnable) {
        long startTime = new Date().getTime();
        //System.out.println("Thread " + Thread.currentThread().getId() + " started (event)");

        Thread thread = new Thread(runnable);
        thread.start();

        long time = new Date().getTime() - startTime;
        //System.out.println("Thread " + Thread.currentThread().getId() + " finished (event) (" + time + "ms)");
    }
}
