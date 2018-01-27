package sr.will.jarvis.modules.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.admin.ModuleAdmin;

import java.util.HashMap;

public class CommandClear extends Command {
    private ModuleAdmin module;
    private HashMap<String, Integer> messagesDeleted = new HashMap<>();

    public CommandClear(ModuleAdmin module) {
        super("clear", "clear [amount]", "Deletes the specified number of messages from the current channel. Default is 10", null);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkBotPermission(message, Permission.MESSAGE_MANAGE);
        checkUserPermission(message, Permission.MESSAGE_MANAGE);

        TextChannel channel = message.getTextChannel();

        int total = 10;

        if (args.length == 1) {
            total = Integer.parseInt(args[0]);
        }

        messagesDeleted.put(channel.getId(), 0);
        deleteFromChannel(channel, total, total);
    }

    public void deleteFromChannel(TextChannel channel, int total, int current) {
        if (current <= 0) {
            sendSuccessMessage(channel, "The last " + total + " messages have been deleted");
            messagesDeleted.remove(channel.getId());
            return;
        }

        final int x;
        if (current > 100) {
            x = 100;
        } else {
            x = current;
        }

        final int newCurrent = current - x;

        System.out.println("======================");
        System.out.println("total:   " + total);
        System.out.println("current: " + newCurrent);
        System.out.println("x:       " + x);

        channel.getHistory().retrievePast(x).queue(messages -> {
            try {
                channel.deleteMessages(messages).queue(success -> {
                    incrementClearedMessages(channel, total, newCurrent, messages.size());
                }, failure -> {
                    System.out.println("well shit.");
                });
            } catch (IllegalArgumentException e) {
                if (e.getMessage().startsWith("Message Id provided was older than 2 weeks.")) {
                    for (int y = 0; y < messages.size(); y += 1) {
                        messages.get(y).delete().queue(success -> {
                            incrementClearedMessages(channel, total, newCurrent, 1);
                        });
                    }
                }
            }
        });
    }

    public void incrementClearedMessages(TextChannel channel, int total, int current, int amount) {
        System.out.println(messagesDeleted.toString());
        messagesDeleted.replace(channel.getId(), messagesDeleted.get(channel.getId()) + amount);

        if (messagesDeleted.get(channel.getId()) >= total || messagesDeleted.get(channel.getId()) % 100 == 0) {
            deleteFromChannel(channel, total, current);
        }
    }
}
