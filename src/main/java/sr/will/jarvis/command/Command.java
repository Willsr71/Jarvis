package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.exception.BotPermissionException;
import sr.will.jarvis.exception.ModuleNotEnabledException;
import sr.will.jarvis.exception.UserPermissionException;
import sr.will.jarvis.module.Module;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Command {
    private String name;
    private String usage;
    private String description;
    private Module module;

    protected Command(String name, String usage, String description, Module module) {
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.module = module;
    }

    public abstract void execute(Message message, String... args);

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public Module getModule() {
        return module;
    }

    public boolean isModuleEnabled(long guildId) {
        if (module == null) {
            return true;
        }

        return module.isEnabled(guildId);
    }

    protected void checkUserPermission(Message message, Permission permission) {
        if (!message.getGuild().getMember(message.getAuthor()).hasPermission(permission)) {
            sendFailureMessage(message, "You don't have permission for that (" + permission.getName() + ")");
            throw new UserPermissionException(permission, message.getGuild());
        }
    }

    protected void checkBotPermission(Message message, Permission permission) {
        if (!message.getGuild().getMember(message.getJDA().getSelfUser()).hasPermission(permission)) {
            sendFailureMessage(message, "I do not have the required permission (" + permission.getName() + ") for that");
            throw new BotPermissionException(permission, message.getGuild());
        }
    }

    public static void checkModuleEnabled(Message message, Module module) {
        if (!module.isEnabled(message.getGuild().getIdLong())) {
            sendFailureMessage(message, "Module \"" + module.getName() + "\" is not enabled on this server");
            throw new ModuleNotEnabledException(module, message.getGuild());
        }
    }

    public static User getMentionedUser(Message message, String... args) {
        if (message.getMentionedUsers().size() != 0) {
            return message.getMentionedUsers().get(0);
        }

        for (String arg : args) {
            try {
                return Jarvis.getJda().getUserById(arg);
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return null;
    }

    public static String condenseArgs(String joiner, String... args) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String arg : args) {
            stringBuilder.append(arg).append(joiner);
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static String condenseArgs(String... args) {
        return condenseArgs(" ", args);
    }

    public static String capitalizeProperly(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1, string.length()).toLowerCase();
    }

    public static void sendSuccessEmote(Message message) {
        message.addReaction("\uD83D\uDC4C").queue();
    }

    public static void sendSuccessMessage(TextChannel channel, String string, boolean delete, Message... messagesToDelete) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Success!", null)
                .setColor(Color.GREEN)
                .setDescription(string);

        if (delete) {
            channel.sendMessage(embed.build()).queue(success -> {
                new Thread(() -> {
                    try {
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e) {
                    }

                    ArrayList<Message> messages = new ArrayList<>(Arrays.asList(messagesToDelete));
                    messages.add(success);

                    if (messages.size() == 1) {
                        messages.get(0).delete().queue();
                    } else {
                        channel.deleteMessages(messages).queue();
                    }
                }).start();
            });
        } else {
            channel.sendMessage(embed.build()).queue();
        }
    }

    public static void sendSuccessMessage(TextChannel channel, String string, Message... messagesToDelete) {
        sendSuccessMessage(channel, string, true, messagesToDelete);
    }

    public static void sendSuccessMessage(Message message, String string, boolean delete) {
        sendSuccessMessage(message.getTextChannel(), string, delete, message);
    }

    public static void sendSuccessMessage(Message message, String string) {
        sendSuccessMessage(message, string, true);
    }

    public static void sendFailureMessage(Message message, String string) {
        message.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Error", null)
                .setColor(Color.RED)
                .setDescription(string)
                .build()
        ).queue();
    }
}
