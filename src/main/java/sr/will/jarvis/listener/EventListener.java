package sr.will.jarvis.listener;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;
import sr.will.jarvis.module.chatbot.ModuleChatBot;
import sr.will.jarvis.module.levels.ModuleLevels;
import sr.will.jarvis.module.smashbot.ModuleSmashBot;

import java.util.Date;

public class EventListener extends ListenerAdapter {
    private Jarvis jarvis;

    private ModuleAdmin moduleAdmin;
    private ModuleChatBot moduleChatBot;
    private ModuleLevels moduleLevels;
    private ModuleSmashBot moduleSmashBot;

    public EventListener(Jarvis jarvis) {
        this.jarvis = jarvis;
        this.moduleAdmin = (ModuleAdmin) jarvis.moduleManager.getModule("admin");
        this.moduleChatBot = (ModuleChatBot) jarvis.moduleManager.getModule("chatbot");
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

            if (moduleAdmin.muteManager.isMuted(event.getGuild().getIdLong(), event.getAuthor().getIdLong())) {
                event.getMessage().delete().queue();
                System.out.println("deleting message");

                if (event.getGuild().getMember(event.getAuthor()).hasPermission(Permission.ADMINISTRATOR)) {
                    return;
                }

                moduleAdmin.muteManager.setup(event.getGuild());
                return;
            }

            if (event.getMessage().getContent().startsWith("!")) {
                jarvis.commandManager.executeCommand(event.getMessage());
                return;
            }

            if (moduleChatBot.isBotChannel(event.getChannel().getIdLong())) {
                if (event.getMessage().getContent().startsWith("<")) {
                    return;
                }

                moduleChatBot.sendResponse(event.getMessage());
                return;
            }

            // moduleLevels.increase(event.getGuild().getId(), event.getAuthor().getId(), event.getTextChannel());
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
            moduleAdmin.muteManager.setup(event.getGuild());
        });
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        startThread(() -> {
            if (!moduleAdmin.isEnabled(event.getGuild().getIdLong())) {
                return;
            }

            moduleAdmin.muteManager.processNewMember(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong());
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
                int maxReactions = 0;
                for (MessageReaction reaction : message.getReactions()) {
                    if (reaction.getEmote().isEmote()) {
                        continue;
                    }

                    if (!jarvis.config.discord.pinEmotes.contains(reaction.getEmote().getName())) {
                        continue;
                    }

                    if (reaction.getCount() > maxReactions) {
                        maxReactions = reaction.getCount();
                    }
                }

                if (maxReactions >= 5) {
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
                int maxReactions = 0;
                for (MessageReaction reaction : message.getReactions()) {
                    if (reaction.getCount() > maxReactions) {
                        maxReactions = reaction.getCount();
                    }
                }

                if (maxReactions <= 3) {
                    Command.unpinMessage(message);
                }
            });
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
