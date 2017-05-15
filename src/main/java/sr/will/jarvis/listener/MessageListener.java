package sr.will.jarvis.listener;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.admin.ModuleAdmin;
import sr.will.jarvis.module.chatbot.ModuleChatBot;
import sr.will.jarvis.module.levels.ModuleLevels;

import java.util.Date;

public class MessageListener extends ListenerAdapter {
    private Jarvis jarvis;

    private ModuleAdmin moduleAdmin;
    private ModuleChatBot moduleChatBot;
    private ModuleLevels moduleLevels;

    public MessageListener(Jarvis jarvis) {
        this.jarvis = jarvis;
        this.moduleAdmin = (ModuleAdmin) jarvis.moduleManager.getModule("admin");
        this.moduleChatBot = (ModuleChatBot) jarvis.moduleManager.getModule("chatbot");
        this.moduleLevels = (ModuleLevels) jarvis.moduleManager.getModule("levels");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Thread thread = new Thread(() -> {
            long startTime = new Date().getTime();
            System.out.println("Thread " + Thread.currentThread().getId() + " started (event)");

            processEvent(event);

            long time = new Date().getTime() - startTime;
            System.out.println("Thread " + Thread.currentThread().getId() + " finished (event) (" + time + "ms)");
        });

        thread.start();
    }

    private void processEvent(MessageReceivedEvent event) {
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
    }
}
