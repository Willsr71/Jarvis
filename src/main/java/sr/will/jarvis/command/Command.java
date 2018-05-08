package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.exception.BotPermissionException;
import sr.will.jarvis.exception.ModuleNotEnabledException;
import sr.will.jarvis.exception.UserPermissionException;
import sr.will.jarvis.module.Module;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    protected void sendUsage(Message message) {
        sendFailureMessage(message, getUsage());
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
            sendFailureMessage(message, "Module \"" + module.getDescription().getName() + "\" is not enabled on this server");
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

    public static MessageChannel getMentionedChannel(Message message, String... args) {
        if (message.getMentionedChannels().size() != 0) {
            return message.getMentionedChannels().get(0);
        }

        return null;
    }

    public static int getMaxApplicableReactionCount(List<MessageReaction> reactions, List<String> applicableReactions) {
        int maxReactions = 0;
        for (MessageReaction reaction : reactions) {
            if (reaction.getReactionEmote().isEmote()) {
                continue;
            }

            if (!applicableReactions.contains(reaction.getReactionEmote().getName())) {
                continue;
            }

            if (reaction.getCount() > maxReactions) {
                maxReactions = reaction.getCount();
            }
        }

        return maxReactions;
    }

    public static String getFiller(int len) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int x = 0; x < len + 1; x += 1) {
            stringBuilder.append(".");
        }

        return stringBuilder.toString();
    }

    public static String getRolesAsString(List<Role> roles) {
        if (roles.size() == 0) {
            return "";
        }

        StringBuilder roleList = new StringBuilder();
        for (Role role : roles) {
            roleList.append(role.getName()).append(", ");
        }
        roleList.setLength(roleList.length() - 2);

        return roleList.toString();
    }

    public static String getGuildsAsString(List<Guild> guilds) {
        StringBuilder guildList = new StringBuilder();
        for (Guild guild : guilds) {
            guildList.append(guild.getName()).append(", ");
        }
        guildList.setLength(guildList.length() - 2);

        return guildList.toString();
    }

    public static String condenseArgs(String joiner, String... args) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String arg : args) {
            stringBuilder.append(arg).append(joiner);
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static String condenseArgs(String[] args) {
        return condenseArgs(" ", args);
    }

    public static String condenseArgs(String[] args, int start) {
        return condenseArgs(Arrays.copyOfRange(args, start, args.length));
    }

    public static String capitalizeProperly(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1, string.length()).toLowerCase();
    }

    public static ArrayList<String> getMessageIds(java.util.List<Message> messages) {
        ArrayList<String> messageIds = new ArrayList<>();
        for (Message message : messages) {
            messageIds.add(message.getId());
        }

        return messageIds;
    }

    public static void pinMessage(Message message) {
        message.getChannel().getPinnedMessages().queue((java.util.List<Message> pinnedMessages) -> {
            if (getMessageIds(pinnedMessages).contains(message.getId())) {
                return;
            }

            if (pinnedMessages.size() == 50) {
                sendFailureMessage(message, "Pinned message limit reached");
                return;
            }

            message.pin().queue();
        });
    }

    public static void unpinMessage(Message message) {
        message.getChannel().getPinnedMessages().queue((List<Message> pinnedMessages) -> {
            if (!getMessageIds(pinnedMessages).contains(message.getId())) {
                return;
            }

            message.unpin().queue();
        });
    }

    public static void sendSuccessEmote(Message message) {
        message.addReaction("\uD83D\uDC4C").queue();
    }

    public static void sendFailureEmote(Message message) {
        message.addReaction("\u274C").queue();
    }

    public static void sendSuccessMessage(Message message, String string) {
        message.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Success!", null)
                .setColor(Color.GREEN)
                .setDescription(string)
                .build()
        ).queue();
    }

    public static void sendFailureMessage(Message message, String string) {
        message.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Error!", null)
                .setColor(Color.RED)
                .setDescription(string)
                .build()
        ).queue();
    }
}
