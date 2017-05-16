package sr.will.jarvis.listener;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.admin.ModuleAdmin;
import sr.will.jarvis.module.chatbot.ModuleChatBot;
import sr.will.jarvis.module.levels.ModuleLevels;

import java.util.Date;

public class EventListener extends ListenerAdapter {
    private Jarvis jarvis;

    private ModuleAdmin moduleAdmin;
    private ModuleChatBot moduleChatBot;
    private ModuleLevels moduleLevels;

    public EventListener(Jarvis jarvis) {
        this.jarvis = jarvis;
        this.moduleAdmin = (ModuleAdmin) jarvis.moduleManager.getModule("admin");
        this.moduleChatBot = (ModuleChatBot) jarvis.moduleManager.getModule("chatbot");
        this.moduleLevels = (ModuleLevels) jarvis.moduleManager.getModule("levels");
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
            moduleAdmin.muteManager.processNewMember(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong());
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
